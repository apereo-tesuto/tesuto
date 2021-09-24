/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.cccnext.tesuto.preview;

import org.cccnext.tesuto.admin.assembler.view.TestLocationViewDtoAssembler;
import org.cccnext.tesuto.admin.assembler.view.TestLocationViewDtoAssemblerImpl;
import org.cccnext.tesuto.content.client.CompetencyMapDisciplineRestClient;
import org.cccnext.tesuto.content.client.UseItemCategoryRestClient;
import org.cccnext.tesuto.content.service.AssessmentAccessService;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.AssessmentItemServiceImpl;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.content.service.AssessmentServiceImpl;
import org.cccnext.tesuto.content.service.scoring.ExpressionEvaluationService;
import org.cccnext.tesuto.delivery.service.AssemblyService;
import org.cccnext.tesuto.delivery.service.AssessmentSessionService;
import org.cccnext.tesuto.delivery.service.ChildExpressionEvaluationServiceDelivery;
import org.cccnext.tesuto.delivery.service.DeliveryService;
import org.cccnext.tesuto.delivery.service.DeliveryServiceAdapter;
import org.cccnext.tesuto.delivery.service.PsychometricsCalculationServiceImpl;
import org.cccnext.tesuto.delivery.service.SelectionServiceImpl;
import org.cccnext.tesuto.delivery.service.TaskSetService;
import org.cccnext.tesuto.delivery.service.scoring.AssessmentItemScoringServiceImpl;
import org.cccnext.tesuto.delivery.service.scoring.OutcomeProcessingService;
import org.cccnext.tesuto.delivery.service.scoring.OutcomeProcessingServiceImpl;
import org.cccnext.tesuto.preview.repository.AssessmentItemRepositoryCache;
import org.cccnext.tesuto.preview.repository.AssessmentRepositoryCache;
import org.cccnext.tesuto.preview.repository.AssessmentSessionCacheDao;
import org.cccnext.tesuto.preview.repository.TaskSetCacheDao;
import org.cccnext.tesuto.preview.service.AssessmentAccessServiceCache;
import org.cccnext.tesuto.preview.service.CompetencyMapOrderServiceCache;
import org.cccnext.tesuto.user.assembler.view.ProctorViewDtoAssembler;
import org.cccnext.tesuto.user.assembler.view.ProctorViewDtoAssemblerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PreviewConfig {

	public ChildExpressionEvaluationServiceDelivery childExpressionEvaluationServiceDelivery() {
		return new ChildExpressionEvaluationServiceDelivery();
	}

	@Bean("expressionEvaluationCacheUtil")
	public ExpressionEvaluationService ExpressionEvaluationService() {
		ExpressionEvaluationService expressionEvaluationService = new ExpressionEvaluationService();
		expressionEvaluationService.setChildExpressionEvaluationService(childExpressionEvaluationServiceDelivery());
		return expressionEvaluationService;
	}

	@Bean("assessmentItemCacheService")
	AssessmentItemService assessmentItemCacheService(
			@Autowired AssessmentItemRepositoryCache assessmentItemCacheDao) {
		AssessmentItemServiceImpl assessmentItemService = new AssessmentItemServiceImpl();
		assessmentItemService.setAssessmentItemRepository(assessmentItemCacheDao);
		return assessmentItemService;
	}
	
	@Bean("assessmentAccessServiceCache")
	AssessmentAccessService assessmentAccessServiceCache() {
		return new AssessmentAccessServiceCache();
	}


	@Bean("assessmentCacheService")
	AssessmentService assessmentCacheService(
			@Autowired @Qualifier("assessmentCacheDao") AssessmentRepositoryCache assessmentCacheDao,
			@Autowired @Qualifier("assessmentItemCacheService") AssessmentItemService assessmentItemCacheService) {
		AssessmentServiceImpl assessmentService = new AssessmentServiceImpl();
		assessmentService.setAssessmentItemService(assessmentItemCacheService);
		assessmentService.setAssessmentRepository(assessmentCacheDao);
		assessmentService.setAssessmentAccessService(new AssessmentAccessServiceCache());
		return assessmentService;
	}


	@Bean("assessmentSessionCacheService")
	AssessmentSessionService assessmentSessionCacheService(
			@Autowired @Qualifier("assessmentSessionCacheDao") AssessmentSessionCacheDao assessmentSessionCacheDao) {
		AssessmentSessionService assessmentSessionService = new AssessmentSessionService();
		assessmentSessionService.setDao(assessmentSessionCacheDao);
		return assessmentSessionService;
	}

	@Bean("taskSetCacheService")
	TaskSetService TaskSetService(
			@Autowired @Qualifier("assessmentItemCacheService") AssessmentItemService assessmentItemCacheService) {
		TaskSetService taskSetService = new TaskSetService();
		taskSetService.setScoringService(new AssessmentItemScoringServiceImpl());
		taskSetService.setSelectionService(new SelectionServiceImpl());
		taskSetService.setAssessmentItemReader(assessmentItemCacheService);
		taskSetService.setCategoryReader(new UseItemCategoryRestClient());
		return taskSetService;
	}

	@Bean("competencyMapOrderServiceCache")
	CompetencyMapOrderServiceCache CompetencyMapOrderServiceCache() {
		return new CompetencyMapOrderServiceCache();
	}

	@Bean("deliveryService")
	DeliveryService deliveryService(
			@Autowired @Qualifier("assessmentSessionCacheDao") AssessmentSessionCacheDao assessmentSessionCacheDao,
			@Autowired @Qualifier("taskSetDao") TaskSetCacheDao taskSetDao,
			@Autowired @Qualifier("assessmentCacheService") AssessmentService assessmentReader,
			@Autowired @Qualifier("taskSetCacheService") TaskSetService taskSetService,
			@Autowired @Qualifier("expressionEvaluationCacheUtil") ExpressionEvaluationService expressionEvaluationService,
			@Autowired @Qualifier("assessmentSessionResultCacheService") OutcomeProcessingService outcomeProcessingService) {
		DeliveryService deliveryService = new DeliveryService();
		deliveryService.setDao(assessmentSessionCacheDao);
		deliveryService.setTaskSetDao(taskSetDao);
		deliveryService.setAssessmentReader(assessmentReader);
		deliveryService.setTaskSetService(taskSetService);
		deliveryService.setExpressionEvaluationService(expressionEvaluationService);
		deliveryService.setOutcomeProcessingService(outcomeProcessingService);

		return deliveryService;
	}

	@Bean("assessmentSessionResultCacheService")
	OutcomeProcessingService assessmentSessionResultCacheService(
			@Autowired @Qualifier("assessmentItemCacheService") AssessmentItemService assessmentItemReader,
			@Autowired @Qualifier("assessmentSessionCacheDao") AssessmentSessionCacheDao assessmentSessionCacheDao) {
		OutcomeProcessingServiceImpl assessmentSessionResultCacheService = new OutcomeProcessingServiceImpl();
		PsychometricsCalculationServiceImpl psychometricsCalculationService = new PsychometricsCalculationServiceImpl();
		assessmentSessionResultCacheService.setPsychometricsCalculationService(psychometricsCalculationService);
		assessmentSessionResultCacheService.setCategoryReader(new UseItemCategoryRestClient());
		assessmentSessionResultCacheService.setAssessmentItemReader(assessmentItemReader);
		assessmentSessionResultCacheService.setDao(assessmentSessionCacheDao);
		return assessmentSessionResultCacheService;
	}
	
	@Bean("assessmentSessionCacheAssemblyService")
	AssemblyService assessmentSessionCacheAssemblyService(@Autowired @Qualifier("assessmentItemCacheService") AssessmentItemService assessmentItemReader,
			@Autowired @Qualifier("taskSetCacheService") TaskSetService taskSetService) {
		AssemblyService assessmentSessionCacheAssemblyService = new AssemblyService();
		assessmentSessionCacheAssemblyService.setAssessmentItemReader(assessmentItemReader);
		assessmentSessionCacheAssemblyService.setTaskSetService(taskSetService);
		return assessmentSessionCacheAssemblyService;
	}
	
	@Bean("deliveryCacheServiceAdapter") 
	DeliveryServiceAdapter deliveryCacheServiceAdapter(@Autowired @Qualifier("assessmentSessionCacheAssemblyService") AssemblyService assemblyService,
			@Autowired @Qualifier("deliveryService") DeliveryService deliveryService,
			@Autowired @Qualifier("assessmentItemCacheService") AssessmentItemService assessmentItemReader,
			@Autowired @Qualifier("taskSetCacheService") TaskSetService taskSetService){
		DeliveryServiceAdapter deliveryCacheServiceAdapter = new DeliveryServiceAdapter();
		deliveryCacheServiceAdapter.setAssemblyService(assemblyService);
		deliveryCacheServiceAdapter.setDeliveryService(deliveryService);
		deliveryCacheServiceAdapter.setAssessmentItemReader(assessmentItemReader);
		deliveryCacheServiceAdapter.setTaskSetService(taskSetService);
		return deliveryCacheServiceAdapter;
	}
	
	@Bean("competencyMapDisciplineService")
	CompetencyMapDisciplineRestClient CompetencyMapDisciplineRestClient() {
		return new CompetencyMapDisciplineRestClient();
	}
	
	@Bean("proctorViewDtoAssembler")
	ProctorViewDtoAssembler proctorViewDtoAssemblerImpl() {
		return new ProctorViewDtoAssemblerImpl();
	}
	
	@Bean("testLocationViewDtoAssembler")
	TestLocationViewDtoAssembler testLocationViewDtoAssemblerImpl() {
		return new TestLocationViewDtoAssemblerImpl();
	}
	
	
}
