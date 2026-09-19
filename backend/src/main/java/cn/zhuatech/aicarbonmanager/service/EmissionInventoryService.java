/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aicarbonmanager.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;

import java.math.*;
import java.util.*;

@Service
public class EmissionInventoryService {
    private static final Set<String> SCOPES=Set.of("SCOPE_1","SCOPE_2_LOCATION","SCOPE_2_MARKET","SCOPE_3");
    public InventoryResult calculate(@Valid InventoryRequest request) {
        Set<String> lineIds=new HashSet<>();
        Map<String,BigDecimal> totals=new LinkedHashMap<>(); SCOPES.forEach(scope->totals.put(scope,BigDecimal.ZERO));
        List<CalculatedLine> lines=new ArrayList<>(); BigDecimal weightedUncertainty=BigDecimal.ZERO; BigDecimal all=BigDecimal.ZERO; int evidence=0;
        for (ActivityLine line:request.activities()) {
            if (!lineIds.add(line.lineId())) throw new IllegalArgumentException("活动数据行号不能重复: "+line.lineId());
            if (!SCOPES.contains(line.scope())) throw new IllegalArgumentException("不支持的核算范围: "+line.scope());
            BigDecimal emission=line.activityAmount().multiply(line.factorKgCo2ePerUnit()).divide(new BigDecimal("1000"),6,RoundingMode.HALF_UP);
            totals.compute(line.scope(),(key,value)->value.add(emission)); all=all.add(emission);
            weightedUncertainty=weightedUncertainty.add(emission.multiply(BigDecimal.valueOf(line.uncertaintyPercent())));
            if (line.evidenceAttached()) evidence++;
            lines.add(new CalculatedLine(line.lineId(),line.sourceName(),line.scope(),emission,line.factorVersion(),line.evidenceAttached()));
        }
        totals.replaceAll((key,value)->value.setScale(3,RoundingMode.HALF_UP)); all=all.setScale(3,RoundingMode.HALF_UP);
        BigDecimal reductions=request.reductionProjects().stream().map(ReductionProject::expectedReductionTco2e).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal projected=all.subtract(reductions).max(BigDecimal.ZERO).setScale(3,RoundingMode.HALF_UP);
        BigDecimal targetGap=projected.subtract(request.targetTco2e()).max(BigDecimal.ZERO).setScale(3,RoundingMode.HALF_UP);
        int coverage=(int)Math.round(evidence*100.0/request.activities().size());
        BigDecimal uncertainty=all.signum()==0?BigDecimal.ZERO:weightedUncertainty.divide(all,2,RoundingMode.HALF_UP);
        List<String> warnings=new ArrayList<>();
        if (coverage<90) warnings.add("凭证覆盖率低于90%，核证前需补齐原始单据");
        if (targetGap.signum()>0) warnings.add("现有减排项目实施后仍高于目标 "+targetGap+" tCO2e");
        if (totals.get("SCOPE_2_LOCATION").signum()>0 && totals.get("SCOPE_2_MARKET").signum()>0) warnings.add("范围二已同时计算位置法与市场法，披露时请分别列示而非重复汇总");
        String status=coverage<90?"EVIDENCE_GAP":targetGap.signum()>0?"TARGET_GAP":"READY_FOR_VERIFICATION";
        return new InventoryResult(status,totals,all,reductions.setScale(3,RoundingMode.HALF_UP),projected,targetGap,coverage,uncertainty,lines,warnings);
    }

    public record InventoryRequest(@NotEmpty List<@Valid ActivityLine> activities,
                                   List<@Valid ReductionProject> reductionProjects,
                                   @NotNull @DecimalMin("0") BigDecimal targetTco2e) {
        public InventoryRequest { reductionProjects=reductionProjects==null?List.of():List.copyOf(reductionProjects); }
    }
    public record ActivityLine(@NotBlank String lineId,@NotBlank String sourceName,@NotBlank String scope,
                               @NotNull @DecimalMin("0") BigDecimal activityAmount,
                               @NotNull @DecimalMin("0") BigDecimal factorKgCo2ePerUnit,@NotBlank String factorVersion,
                               @Min(0) @Max(100) int uncertaintyPercent,boolean evidenceAttached) {}
    public record ReductionProject(@NotBlank String projectCode,@NotNull @DecimalMin("0") BigDecimal expectedReductionTco2e) {}
    public record CalculatedLine(String lineId,String sourceName,String scope,BigDecimal emissionTco2e,String factorVersion,boolean evidenceAttached) {}
    public record InventoryResult(String status,Map<String,BigDecimal> scopeTotalsTco2e,BigDecimal grossEmissionTco2e,
                                  BigDecimal expectedReductionTco2e,BigDecimal projectedEmissionTco2e,BigDecimal targetGapTco2e,
                                  int evidenceCoveragePercent,BigDecimal weightedUncertaintyPercent,List<CalculatedLine> lines,List<String> warnings) {}
}
