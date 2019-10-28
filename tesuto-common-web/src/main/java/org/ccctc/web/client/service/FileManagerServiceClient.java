package org.ccctc.web.client.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Configuration
@PropertySource(value = "classpath:filemanagerService.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:/opt/ccc/config/filemanagerService.properties", ignoreResourceNotFound = true)
@Slf4j
public class FileManagerServiceClient extends AbstractServiceClient {
    private ObjectMapper mapper = new ObjectMapper();

    public void deleteFile(String fileWithPath) {
        String url = getDeleteFileUrl(fileWithPath);
        log.debug("Attempting to delete file {}", fileWithPath);
        log.debug("Sending DELETE call to: {}", url);
        restHandler.makeAuthenticatedRestCall(HttpMethod.DELETE, url, new HashMap<>());
    }
    
    /**
     * If we can determine (guess) at miscode and cccid from the file path, try to return those values in the map.
     * @return
     */
    public Map<String, Object> getDetailFromFilePath(String filePath) {
        boolean isPublic = filePath.contains("public");
        Map<String, Object> results = new HashMap<>();
        if (isPublic) {
            String filename = filePath.substring(filePath.lastIndexOf("public") + 7);
            List<String> list = new ArrayList<String>(Arrays.asList(filename.split("/")));
            // If the first part of the file hierarchy is 3 chars long, we'll assume it is an miscode
            String possibleMis = list.get(0);
            if (possibleMis.length() == 3) {
                results.put("cccmiscode", possibleMis);
            }
        } else {
            // files/private/ZZ1/users/AAA5364/000.png
            //                0    1      2       3
            String filename = filePath.substring(filePath.lastIndexOf("private") + 8);
            List<String> list = new ArrayList<String>(Arrays.asList(filename.split("/")));
            results.put("cccmiscode", list.get(0));
            results.put("uploadedby", list.get(2));
            results.put("cccid", list.get(2));
        }
        return results;
    }
    
    @SuppressWarnings("rawtypes")
    public Map<String, Object> getFileMetadata(String fileWithPath) {
        String url = getMetadataUrl(fileWithPath);
        log.debug("Attempting to fetch metadata with URL {}", url);
        ResponseEntity re = restHandler.makeAuthenticatedRestCall(HttpMethod.GET, url, new HashMap<>());
        try {
            return mapper.convertValue(re.getBody(), new TypeReference<Map<String, Object>>() {
            });
        }
        catch (Exception e) {
            return new HashMap<>();
        }
    }

    private String getDeleteFileUrl(String fileWithPath) {
        boolean isPublic = fileWithPath.contains("public");
        StringBuilder result = new StringBuilder(serviceUrl);
        result.append("/");
        String filename = "";
        if (isPublic) {
            result.append("/file/");
            filename = fileWithPath.substring(fileWithPath.lastIndexOf("public") + 7);
            result.append(filename);
            
        } else {
            //                0    1      2       3
            // files/private/ZZ1/users/AAA5364/000.png
            filename = fileWithPath.substring(fileWithPath.lastIndexOf("private") + 8);
            result.append("v1/");
            List<String> list = new ArrayList<String>(Arrays.asList(filename.split("/")));
            result.append(list.get(0));
            result.append("/");
            result.append(list.get(1));
            result.append("/");
            result.append(list.get(2));
            result.append("/files/");
            result.append(list.get(3));
        }
        return result.toString();
    }
    
    private String getMetadataUrl(String fileWithPath) {
        boolean isPublic = fileWithPath.contains("public");
        StringBuilder result = new StringBuilder(serviceUrl);
        result.append("/");
        String filename = "";
        if (isPublic) {
            filename = fileWithPath.substring(fileWithPath.lastIndexOf("public") + 7);
            result.append("metadata/file/");
            List<String> list = new ArrayList<String>(Arrays.asList(filename.split("/")));
            result.append(list.get(list.size() - 1));
            result.append("/fromHierarchy/");
            for (int i = 0; i < (list.size() - 1); i++) {
                result.append(list.get(i));
                if (i < (list.size() - 2)) {
                    result.append("/");
                }
            }
        } else {
            // files/private/ZZ1/users/AAA5364/000.png
            filename = fileWithPath.substring(fileWithPath.lastIndexOf("private") + 8);
            result.append("v1/");
            List<String> list = new ArrayList<String>(Arrays.asList(filename.split("/")));
            result.append(list.get(0));
            result.append("/");
            result.append(list.get(1));
            result.append("/");
            result.append(list.get(2));
            result.append("/files/metadata/");
            result.append(list.get(3));
        }
        return result.toString();
    }

    @Override
    protected void init(final Environment environment) {
        super.init(environment);
        serviceUrl = adjustForEnv(
                        environment.getProperty("filemanager.url", "https://gateway.{{ENV}}.cccmypath.org/ccc/api/filemanager"));
        buildServiceAccountManager(environment);
        log.debug("********** Using service URL {}", serviceUrl);
    }

    public static void main(String[] args) {
        FileManagerServiceClient client = new FileManagerServiceClient();
        client.init(new MockEnvironment());
        System.out.println(client.getFileMetadata("files/private/ZZ1/users/AAA5364/000.png"));
    }
}
