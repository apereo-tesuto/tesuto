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
package org.cccnext.tesuto.importer.qti.test;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto;
import org.cccnext.tesuto.content.service.CompetencyServiceImpl;
import org.cccnext.tesuto.importer.service.competency.CompetencyImportService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;

@RunWith(SpringJUnit4ClassRunner.class)
public class ComptencyImportServiceTest {

	@Autowired
	CompetencyImportService importService;
	
	@Autowired
	CompetencyServiceImpl competencyService;
	
	
	@Configuration
	@EnableMongoRepositories(basePackages = {"org.cccnext.tesuto.content.repository.mongo"})
	@ImportResource(locations = { "classpath:/commonImportContext.xml"})
	static class ComptencyImportTestConfiguration extends AbstractMongoConfiguration {

		@Override
		protected String getDatabaseName() {
			return "test";
		}
		
		@Override
		public Mongo mongo() {
			// uses fongo for in-memory tests
			return new Fongo("mongo-test").getMongo();
		}
		
		@Override
		protected String getMappingBasePackage() {
			return "org.cccnext.tesuto.content.model.competency";
		}

	}
	

	
	@Test
	public void competencyVersion2Import() throws Exception {
		URL file = ResourceUtils.getURL("classpath:competencySample/competency-v2.xml");
		CompetencyDto dto = importService.parseCompetencyResources(file.toURI(), 0, "math");
		Assert.assertNotNull(dto);
	}

	@Test
	public void testVersionIncrement() throws Exception {
		File file = ResourceUtils.getFile("classpath:competencySample/compMap.zip");
		List<CompetencyMapDto> mapDtos = importService.createCompetencyMapFromFile(file, false);
		
		List<CompetencyMapDto> nextMapDtos = importService.createCompetencyMapFromFile(file, false);
		for (CompetencyMapDto competencyMapDto : nextMapDtos) {
			Assert.assertEquals(2, competencyMapDto.getVersion());
			assertComptencyRefs( competencyMapDto.getCompetencyRefs());
		}
	
	}

	private void assertComptencyRefs(List<CompetencyRefDto> competencyRefs) {
		if (CollectionUtils.isNotEmpty(competencyRefs)) {
			for (CompetencyRefDto ref : competencyRefs) {
				Assert.assertEquals(2, ref.getVersion());
				CompetencyDto competencyDto = competencyService.readByDisciplineIdentifierAndVersion(ref.getDiscipline(), ref.getCompetencyIdentifier(), ref.getVersion());
				assertComptencyRefs( competencyDto.getChildCompetencyDtoRefs());
			}
		}
	}
}
