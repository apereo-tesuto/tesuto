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
package org.ccctc.common.droolsdb.dynamodb.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.exceptions.SaveException;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolsdb.dynamodb.dao.ApplicationDAO;
import org.ccctc.common.droolsdb.dynamodb.model.Application;
import org.ccctc.common.droolsdb.dynamodb.utils.ApplicationDTOMapper;
import org.ccctc.common.droolsdb.services.IEngineService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * As of v2.0.0 of the drools-engine and drools-common libraries, "applications"
 * have been renamed/refactored as "engines"
 */
public class EngineServiceImpl implements IEngineService, InitializingBean {
	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationDTOMapper mapper = new ApplicationDTOMapper();

	@Autowired
	private DynamoDBTableService tableService;

	@Override
	public void afterPropertiesSet() throws Exception {
		tableService.createTable(Application.TABLE_NAME, Application.class);
	}

	@Override
	public void delete(EngineDTO engineDTO) {
		applicationDAO.deleteById(engineDTO.getName());
	}

	@Override
	public EngineDTO getEngineByName(String name) {
		Application application = null;
		if (StringUtils.isBlank(name)) {
			throw new ObjectNotFoundException("Cannot retrieve Application by name if name is blank");
		}
		try {
			application = applicationDAO.findById(name).get();
		} catch (NoSuchElementException ex) {
			throw new ObjectNotFoundException("Cannot retrieve Application[" + name + "], name not found");
		}
		if (application == null) {
			throw new ObjectNotFoundException("Cannot retrieve Application[" + name + "], name not found");
		}
		EngineDTO engineDTO = mapper.mapTo(application);
		return engineDTO;
	}

	@Override
	public List<EngineDTO> getEngines() {
		List<Application> applications = (List<Application>) applicationDAO.findAll();

		List<EngineDTO> engineDTOs = mapper.mapTo(applications);
		return engineDTOs;
	}

	@Override
	public List<EngineDTO> getEnginesByNames(Iterable<String> ids) {
		return mapper.mapTo(applicationDAO.findAllById(ids));
	}

	@Override
	public List<EngineDTO> getEnginesByStatus(String status) {
		if (StringUtils.isBlank(status)) {
			return getEngines();
		}
		List<Application> applications = (List<Application>) applicationDAO.findByStatus(status);
		List<EngineDTO> engineDTOs = mapper.mapTo(applications);
		return engineDTOs;
	}

	@Override
	public EngineDTO save(EngineDTO engineDTO) {
		if (engineDTO.getName() == null) {
			throw new SaveException("Application must have an assigned name");
		}

		Application application = mapper.mapFrom(engineDTO);
		applicationDAO.save(application);
		return engineDTO;
	}
}
