/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aicarbonmanager.controller;
import cn.zhuatech.aicarbonmanager.common.ApiResponse;
import cn.zhuatech.aicarbonmanager.service.DomainDecisionService;
import cn.zhuatech.aicarbonmanager.service.EmissionInventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/domain") public class DomainDecisionController {
 private final DomainDecisionService service; private final EmissionInventoryService emissionInventoryService;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public DomainDecisionController(DomainDecisionService service, EmissionInventoryService emissionInventoryService){this.service=service;this.emissionInventoryService=emissionInventoryService;}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @PostMapping("/decision") public ApiResponse<DomainDecisionService.DecisionResult> assess(@Valid @RequestBody DomainDecisionService.DecisionRequest request){return ApiResponse.ok(service.assess(request));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @PostMapping("/emission-inventory") public ApiResponse<EmissionInventoryService.InventoryResult> inventory(@Valid @RequestBody EmissionInventoryService.InventoryRequest request){return ApiResponse.ok(emissionInventoryService.calculate(request));}
}
