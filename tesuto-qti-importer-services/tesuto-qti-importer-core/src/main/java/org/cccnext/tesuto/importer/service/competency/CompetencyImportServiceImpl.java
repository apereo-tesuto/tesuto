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
package org.cccnext.tesuto.importer.service.competency;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineService;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.cccnext.tesuto.content.service.CompetencyService;
import org.cccnext.tesuto.domain.util.ZipFileExtractor;
import org.cccnext.tesuto.importer.qti.exception.InvalidCompetencyMapPackageException;
import org.cccnext.tesuto.importer.service.upload.ImportService;
import org.cccnext.tesuto.importer.structs.ImportFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.ImsManifestException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

/**
 * Created by jasonbrown on 6/22/16.
 */
@Service(value = "competencyImportService")
public class CompetencyImportServiceImpl implements CompetencyImportService {

	private final String DISCIPLINE = "discipline";
	private final String IDENTIFIER = "identifier";

	@Autowired
	ImportService importService;

	@Autowired
	CompetencyMapService competencyMapService;

	@Autowired
	CompetencyService competencyService;

	@Autowired
	CompetencyMapDisciplineService competencyMapDisciplineService;

	@Autowired
	ZipFileExtractor zipFileExtractor;

	@Override
	public File getUploadLocation() {
		return importService.getUploadLocation();
	}

	private XmlMapper initializeXmlMapper() {
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		xmlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		return xmlMapper;
	}

	@Override
	public CompetencyMapDto parseCompetencyMapResources(URI uri, int version, String discipline) throws IOException {
		InputStream inputStream = uri.toURL().openStream();
		return parseCompetencyMapResources(inputStream, version, discipline);
	}

	@Override
	public CompetencyMapDto parseCompetencyMapResources(InputStream inputStream, int version, String discipline)
			throws IOException {
		XmlMapper xmlMapper = initializeXmlMapper();
		CompetencyMapDto competencyMapDto = xmlMapper.readValue(inputStream, CompetencyMapDto.class);
		competencyMapDto.setVersion(version); // This will increment even if there are no changes.
		competencyMapDto.setDiscipline(discipline);
		competencyMapDto
				.setCompetencyRefs(cleanUpChildCompetencyDtoRefs(competencyMapDto.getCompetencyRefs(), discipline));
		return competencyMapDto;
	}

	@Override
	public CompetencyDto parseCompetencyResources(URI uri, int version, String discipline) throws IOException {
		InputStream inputStream = uri.toURL().openStream();
		return parseCompetencyResources(inputStream, version, discipline);
	}

	@Override
	public CompetencyDto parseCompetencyResources(InputStream inputStream, int version, String discipline)
			throws IOException {
		XmlMapper xmlMapper = initializeXmlMapper();
		CompetencyDto competencyDto = xmlMapper.readValue(inputStream, CompetencyDto.class);
		competencyDto.setVersion(version); // This will increment even if there are no changes.
		competencyDto.setDiscipline(discipline);
		if (competencyDto.getChildCompetencyDtoRefs() != null) {
			competencyDto.setChildCompetencyDtoRefs(cleanUpChildCompetencyDtoRefs(
					competencyDto.getChildCompetencyDtoRefs(), competencyDto.getDiscipline()));
		} else {
			competencyDto.setChildCompetencyDtoRefs(null);
		}
		return competencyDto;
	}

	private List<CompetencyRefDto> cleanUpChildCompetencyDtoRefs(List<CompetencyRefDto> competencyRefDtos,
			String parentDiscpline) {
		List<CompetencyRefDto> cleanedRefs = new ArrayList<>();
		for (CompetencyRefDto competencyRefDto : competencyRefDtos) {
			if (StringUtils.isNotBlank(competencyRefDto.getCompetencyIdentifier())) {
				// during the import references might not have disciplines, so we assume the
				// discipline of the parent.
				if (StringUtils.isEmpty(competencyRefDto.getDiscipline())) {
					competencyRefDto.setDiscipline(parentDiscpline);
				}
				cleanedRefs.add(competencyRefDto);
			}
		}
		return cleanedRefs;
	}

	@Override
	public ImportFiles getResourcesURIsFromFile(File uploadLocation, File uploadFile, Boolean isXmlFile)
			throws IOException, XmlResourceNotFoundException, ImsManifestException, URISyntaxException,
			AmazonServiceException, NoSuchAlgorithmException, SAXException, ParserConfigurationException {

		ImportFiles importFiles = new ImportFiles();
		if(!uploadFile.exists()) {
			return getResourcesURIsFromPath(uploadLocation,  uploadFile.getPath(), isXmlFile);
		}

		List<URI> unzippedFiles = null;
		if (!isXmlFile) {
			FileInputStream fileInputStream = new FileInputStream(uploadFile);
			unzippedFiles = zipFileExtractor.extract(uploadLocation, fileInputStream);
		} else {
			unzippedFiles = Arrays.asList(uploadFile.toURI());
		}
		return importService.searchForXmlFiles(unzippedFiles, importFiles).getValue();
	}

	@Override
	public ImportFiles getResourcesURIsFromPath(File uploadLocation, String source, Boolean isXmlFile)
			throws IOException, XmlResourceNotFoundException, ImsManifestException, URISyntaxException,
			AmazonServiceException, NoSuchAlgorithmException, SAXException, ParserConfigurationException {

		File uploadFile = moveFileToUniqueDirectory(uploadLocation, source);

		return getResourcesURIsFromFile(uploadLocation, uploadFile, isXmlFile);
	}

	private File moveFileToUniqueDirectory(File uploadDirectory, String source) throws IOException {
		ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
		StringBuilder uploadPath = new StringBuilder();
		uploadPath.append(uploadDirectory).append('/').append("transcribed").append('/')
				.append(resourceResolver.getResource(source).getFilename());
		final File destination = new File(uploadPath.toString());

		if(!source.contains("classpath"))
			source ="classpath:" + source;
		FileUtils.copyInputStreamToFile(resourceResolver.getResource(source).getInputStream(), destination);

		return destination;
	}

	@Override
	public List<CompetencyMapDto> createCompetencyMapFromFile(final File convertedFile, boolean isXml)
			throws Exception {
		File uploadDirectory = getUploadLocation();
		ImportFiles importFiles = getResourcesURIsFromFile(uploadDirectory, convertedFile, isXml);
		if (CollectionUtils.isEmpty(importFiles.getCompetencyMaps())
				&& CollectionUtils.isEmpty(importFiles.getCompetencies())) {
			throw new InvalidCompetencyMapPackageException("Xml must meet CCC ASSESS schema.");
		}

		try {
			importService.verifySchema(importFiles.getCompetencyMaps());
			importService.verifySchema(importFiles.getCompetencies());
		} catch (SAXException e) {
			throw new InvalidCompetencyMapPackageException(
					String.format("Packaged failed schema validation %s", e.getMessage()));
		}

		// Parse both objects to ensure all schemas are correctly constructed and
		// serialize before we store the objects
		List<CompetencyMapDto> competencyMapDtoList = parseCompetencyMapFiles(importFiles.getCompetencyMaps(),
				uploadDirectory);
		List<CompetencyDto> competencyDtoList = parseCompetencyFiles(importFiles.getCompetencies(), uploadDirectory);

		FileUtils.deleteDirectory(uploadDirectory);
		competencyService.create(competencyDtoList);
		for (CompetencyMapDto competencyMapDto : competencyMapDtoList) {
			for (CompetencyRefDto competencyRef : competencyMapDto.getCompetencyRefs()) {
				CompetencyDto dto = competencyService.readLatestPublishedVersion(competencyMapDto.getDiscipline(),
						competencyRef.getCompetencyIdentifier());
				competencyRef.setVersion(dto.getVersion());
			}

		}
		return competencyMapService.create(competencyMapDtoList);
	}

	// TODO refactor duplicate code
	private List<CompetencyMapDto> parseCompetencyMapFiles(List<URI> competencyMapURIs, File uploadDirectory)
			throws ParserConfigurationException, SAXException, IOException {
		List<CompetencyMapDto> parsedCompetencyMaps = new LinkedList<>();
		for (URI competencyMapURI : competencyMapURIs) {
			Path path = Paths.get(competencyMapURI);
			String discipline = importService.getTextContentByTagName(path.toFile(), DISCIPLINE);
			String upperDiscipline = discipline.toUpperCase();
			List<CompetencyMapDisciplineDto> validDisciplineDtos = competencyMapDisciplineService.read();

			List<String> validDisciplines = validDisciplineDtos.stream()
					.map(CompetencyMapDisciplineDto::getDisciplineName).collect(Collectors.toList());
			if (!validDisciplines.contains(upperDiscipline)) {
				throw new InvalidCompetencyMapPackageException(String.format("Unsupported discipline %s", discipline));
			}
			int version = competencyMapService.getNextVersion(upperDiscipline);

			CompetencyMapDto competencyMapDto = parseCompetencyMapResources(competencyMapURI, version, upperDiscipline);
			parsedCompetencyMaps.add(competencyMapDto);
		}
		return parsedCompetencyMaps;
	}

	// TODO refactor duplicate code
	private List<CompetencyDto> parseCompetencyFiles(List<URI> competencyURIs, File uploadDirectory)
			throws ParserConfigurationException, SAXException, IOException {
		List<CompetencyDto> parsedCompetencies = new LinkedList<>();
		for (URI competencyURI : competencyURIs) {
			Path path = Paths.get(competencyURI);

			String identifier = importService.getTextContentByTagName(path.toFile(), IDENTIFIER);
			String discipline = importService.getTextContentByTagName(path.toFile(), DISCIPLINE);
			String upperDiscipline = discipline.toUpperCase();
			List<CompetencyMapDisciplineDto> validDisciplineDtos = competencyMapDisciplineService.read();

			List<String> validDisciplines = validDisciplineDtos.stream()
					.map(CompetencyMapDisciplineDto::getDisciplineName).collect(Collectors.toList());
			if (!validDisciplines.contains(upperDiscipline)) {
				throw new InvalidCompetencyMapPackageException(String.format("Unsupported discipline %s", discipline));
			}
			int version = competencyService.getNextVersion(upperDiscipline, identifier);

			CompetencyDto competencyDto = parseCompetencyResources(competencyURI, version, upperDiscipline);
			parsedCompetencies.add(competencyDto);
		}
		return parsedCompetencies;
	}

}
