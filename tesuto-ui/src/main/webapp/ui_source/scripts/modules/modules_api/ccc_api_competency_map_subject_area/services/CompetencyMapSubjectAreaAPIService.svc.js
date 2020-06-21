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
(function () {

    angular.module('CCC.API.CompetencyMapSubjectArea').service('CompetencyMapSubjectAreaAPIService', [

        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'CCCUtils',

        function ($http, ErrorHandlerService, VersionableAPIClass, CCCUtils) {

            /*============ SERVICE DECLARATION ============*/

            var CompetencyMapSubjectAreaAPIService = new VersionableAPIClass({id:'competencyMapDiscipline', resource: 'competency-map-discipline'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            CompetencyMapSubjectAreaAPIService.getSubjectAreas = function () {

                return $http.get(CompetencyMapSubjectAreaAPIService.getBasePath(), {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return CompetencyMapSubjectAreaAPIService;
        }
    ]);

})();

