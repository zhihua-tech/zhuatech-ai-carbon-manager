/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aicarbonmanager.service;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;
import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class DomainDecisionService {
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public DecisionResult assess(DecisionRequest request) { int score=(int)Math.round((request.activityDataCoverage()+request.factorCoverage()+request.verificationPassRate())/3);List<String>actions=new ArrayList<>();if(request.activityDataCoverage()<95){score-=25;actions.add("补齐设施和范围三活动数据");}if(request.factorCoverage()<98){score-=25;actions.add("补充适用地域与年份的排放因子");}if(request.unresolvedAnomalies()>0){score-=Math.min(40,request.unresolvedAnomalies()*8);actions.add("关闭异常排放数据并保留更正证据");}if(!request.methodologyApproved()){score-=45;actions.add("批准组织边界与核算方法学");}if(!request.independentVerified()){score-=35;actions.add("完成独立核证后再披露");}if(request.forecastReductionRate()<request.targetReductionRate())actions.add("补充减排项目以弥合目标差距");return result(score,actions,"REPORT_READY","IMPROVE","BLOCKED",Map.of("dataCoverage",request.activityDataCoverage(),"factorCoverage",request.factorCoverage(),"verificationPassRate",request.verificationPassRate(),"reductionGap",request.targetReductionRate()-request.forecastReductionRate(),"unresolvedAnomalies",request.unresolvedAnomalies())); }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private DecisionResult result(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=80?good:score>=50?warn:bad;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private DecisionResult riskResult(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=70?bad:score>=40?warn:good;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record DecisionRequest(
        @NotBlank String inventoryNo,
        @DecimalMin("0") @DecimalMax("100") double activityDataCoverage,
        @DecimalMin("0") @DecimalMax("100") double factorCoverage,
        @DecimalMin("0") @DecimalMax("100") double verificationPassRate,
        @DecimalMin("0") @DecimalMax("100") double forecastReductionRate,
        @DecimalMin("0") @DecimalMax("100") double targetReductionRate,
        @PositiveOrZero int unresolvedAnomalies,
        boolean methodologyApproved,
        boolean independentVerified) {}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record DecisionResult(String decision,int score,Map<String,Object> metrics,List<String> actions) {}
}
