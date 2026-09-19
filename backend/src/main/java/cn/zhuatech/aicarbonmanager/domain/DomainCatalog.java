/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aicarbonmanager.domain;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class DomainCatalog {
    private final Map<String, WorkflowAction> actions = new LinkedHashMap<>();
    public DomainCatalog() {
        actions.put("CALCULATE", new WorkflowAction("CALCULATE", "提交碳盘查", List.of("草稿"), "待核证", "OPERATOR"));
        actions.put("VERIFY", new WorkflowAction("VERIFY", "批准盘查结果", List.of("待核证"), "待披露", "ADMIN"));
        actions.put("PUBLISH", new WorkflowAction("PUBLISH", "发布碳盘查报告", List.of("待披露"), "已披露", "ADMIN"));
    }
    public String systemName() { return "知华科技AI碳核算与减排管理系统"; }
    public String scene() { return "组织边界、活动数据、排放因子、范围一二三、AI异常、碳盘查、目标、减排情景、核证与披露"; }
    public String initialStatus() { return "草稿"; }
    public String partyLabel() { return "组织/设施/排放源"; }
    public String amountLabel() { return "碳成本"; }
    public String quantityLabel() { return "碳排放量"; }
    public String dueLabel() { return "盘查与披露期限"; }
    public List<ModuleDefinition> modules() { return List.of(
            new ModuleDefinition("BOUNDARY", "组织与运营边界", "管理法人、设施、控制权和盘查范围"),
            new ModuleDefinition("ACTIVITY", "活动数据", "采集能源、燃料、冷媒、物流、采购和差旅数据"),
            new ModuleDefinition("FACTOR", "排放因子", "维护来源、地域、年份、单位和版本"),
            new ModuleDefinition("INVENTORY", "碳排放核算", "计算范围一、范围二和范围三排放"),
            new ModuleDefinition("ANOMALY", "AI异常识别", "识别缺失、突变、重复和异常排放强度"),
            new ModuleDefinition("TARGET", "减排目标", "维护基准年、目标年、路径和责任分解"),
            new ModuleDefinition("SCENARIO", "AI减排情景", "评估节能、绿电、工艺、物流和供应链方案"),
            new ModuleDefinition("VERIFICATION", "核证管理", "管理抽样、证据、调整和独立核证"),
            new ModuleDefinition("DISCLOSURE", "披露与审计", "生成盘查报告并保留方法学和数据血缘")
        ); }
    public Map<String, WorkflowAction> actions() { return Collections.unmodifiableMap(actions); }
    public record ModuleDefinition(String code,String name,String description) {}
    public record WorkflowAction(String code,String label,List<String> from,String to,String requiredRole) {}
}
