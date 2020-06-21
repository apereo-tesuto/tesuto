/*-------------------------------------------------------------------------------
# Copyright © 2019 by California Community Colleges Chancellor's Office
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.  You may obtain a copy
# of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.
#------------------------------------------------------------------------------*/
angular.module('colleges', [])
.service('CollegeService', function($http, $log) {
    var collegeService = {};

    collegeService.getColleges = function() {
       return $http.get('/rules-editor/colleges/')
       .then(function(response) {
           return response.data;
        });
    }
   return collegeService;
})
.controller('CollegeServiceCtrl', function(CollegeService, $scope, $http) {
    var CONTENT_TYPE_JSON = {'Content-Type': 'application/json'};

    $scope.saveCollege = function(formCollege) {
        var selectedCollege = {
            id: $scope.selectedCollegeId,
            cccMisCode: $scope.selectedCollegeCccMisCode,
            description: $scope.selectedCollegeDescription,
            status: $scope.selectedCollegeStatus,
            applications: $scope.selectedCollegeApplications
        };

        $http({url: "/rules-editor/colleges", method: "POST", headers: CONTENT_TYPE_JSON, data: selectedCollege})
            .success(function(data, status, headers, config) {
                $scope.status = status;
                CollegeService.getColleges().then(function(response) {
                    $scope.colleges = response;
                })
            })
            .error(function(data, status, headers, config) {
                console.log("Error saving college", selectedCollege, status);
                $scope.status = status;
                CollegeService.getColleges().then(function(response) {
                    $scope.colleges = response;
                })
            });
        $scope.clearCollege(formCollege);
    };

    $scope.deleteCollege = function(college, formCollege) {
        $http({url: "/rules-editor/colleges/" + college.id, method: "DELETE", headers: CONTENT_TYPE_JSON})
            .success(function(data, status, headers, config) {
                $scope.status = status;
                CollegeService.getColleges().then(function(response) {
                    $scope.colleges = response;
                })
            })
            .error(function(data, status, headers, config) {
                console.log("Error deleting college", college.id, status);
                $scope.status = status;
                CollegeService.getColleges().then(function(response) {
                    $scope.colleges = response;
                })
            });
        $scope.clearCollege(formCollege);
    }

    $scope.editCollege = function(college, formCollege) {
        $scope.selectedCollegeId = college.id;
        $scope.selectedCollegeCccMisCode = college.cccMisCode;
        $scope.selectedCollegeDescription = college.description;
        $scope.selectedCollegeStatus = college.status;
        $scope.selectedCollegeApplications = college.applications;

        if(formCollege) formCollege.$setPristine();
    }

    $scope.editApplication = function(application) {
        $scope.selectedApplicationName = application.name;
        $scope.selectedApplicationDataSource = application.dataSource
        $scope.selectedApplicationGroupId = application.groupId;
        $scope.selectedApplicationArtifactId = application.artifactId;
        $scope.selectedApplicationVersion = application.version;
    }

    $scope.deleteApplication = function(application) {
        delete $scope.selectedCollegeApplications[application.name];
    }

    $scope.updateApplication = function() {
        $scope.selectedCollegeApplications[$scope.selectedApplicationName] = {
            name: $scope.selectedApplicationName,
            dataSource: $scope.selectedApplicationDataSource,
            groupId: $scope.selectedApplicationGroupId,
            artifactId: $scope.selectedApplicationArtifactId,
            version: $scope.selectedApplicationVersion
        };
        $scope.clearApplication();
    }

    $scope.clearCollege = function(formCollege) {
        $scope.status = null;
        $scope.selectedCollegeId = null;
        $scope.selectedCollegeCccMisCode = null;
        $scope.selectedCollegeDescription = null;
        $scope.selectedCollegeStatus = null;
        $scope.selectedCollegeApplications = {};
        $scope.clearApplication();

        if(formCollege) formCollege.$setPristine();
    }

    $scope.clearApplication = function() {
        $scope.selectedApplicationName = null;
        $scope.selectedApplicationDataSource = null;
        $scope.selectedApplicationGroupId = null;
        $scope.selectedApplicationArtifactId = null;
        $scope.selectedApplicationVersion = null;
    }

    $scope.getCollegeApplicationNames = function(applications) {
        var keys = "";
        for (let key of Object.keys(applications)) {
            if (keys != "") {
                keys += ", ";
            }
            keys += key;
        }
        return keys;
    }

    $scope.isInvalidCollege = function() {
        var cccMisCode = $scope.selectedCollegeCccMisCode;
        if ($scope.isBlank(cccMisCode)) {
            return true;
        }
	    return false;
    }

    $scope.isInvalidApplication = function() {
        var applicationName = $scope.selectedApplicationName;
        if ($scope.isBlank(applicationName)) {
            return true;
        }
        var dataSource = $scope.selectedApplicationDataSource;
        if (dataSource == "maven") {
            var groupId = $scope.selectedApplicationGroupId;
            var artifactId = $scope.selectedApplicationArtifactId;
            var version = $scope.selectedApplicationVersion;
            if ($scope.isBlank(groupId) || $scope.isBlank(artifactId) || $scope.isBlank(version)) {
                return true;
            }
        }
        return false;
    }

    $scope.isBlank = function(value) {
        return (!value || value.length == 0);
    }

    // pull fresh set of colleges from database
    CollegeService.getColleges().then(function(response) {
        $scope.colleges = response;
    })

    // clear display
    $scope.clearCollege();
});
