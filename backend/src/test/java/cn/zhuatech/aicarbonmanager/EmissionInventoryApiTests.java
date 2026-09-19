/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aicarbonmanager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest @AutoConfigureMockMvc class EmissionInventoryApiTests {
 @Autowired MockMvc mvc;
 private static final String BODY="""
  {"targetTco2e":5,"activities":[
   {"lineId":"A-1","sourceName":"天然气","scope":"SCOPE_1","activityAmount":1000,"factorKgCo2ePerUnit":2.1,"factorVersion":"CN-2026","uncertaintyPercent":5,"evidenceAttached":true},
   {"lineId":"A-2","sourceName":"外购电力","scope":"SCOPE_2_LOCATION","activityAmount":10000,"factorKgCo2ePerUnit":0.57,"factorVersion":"GRID-2026","uncertaintyPercent":8,"evidenceAttached":true}],
   "reductionProjects":[{"projectCode":"PV-01","expectedReductionTco2e":3}],"targetTco2e":5}
  """;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void calculatesScopesEvidenceUncertaintyAndReductionGap() throws Exception {
  mvc.perform(post("/api/domain/emission-inventory").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(BODY))
   .andExpect(status().isOk()).andExpect(jsonPath("$.data.grossEmissionTco2e").value(7.8)).andExpect(jsonPath("$.data.projectedEmissionTco2e").value(4.8))
   .andExpect(jsonPath("$.data.status").value("READY_FOR_VERIFICATION")).andExpect(jsonPath("$.data.evidenceCoveragePercent").value(100));
 }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void rejectsUnknownScope() throws Exception {
  mvc.perform(post("/api/domain/emission-inventory").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(BODY.replace("SCOPE_1","SCOPE_X")))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("不支持的核算范围: SCOPE_X"));
 }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void inventoryRequiresAuthentication() throws Exception {mvc.perform(post("/api/domain/emission-inventory").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());}
}
