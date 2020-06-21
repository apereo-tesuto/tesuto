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

    // Each module should register their i18 locations with this provider so they can be loaded into the $translate provider
    // The only purpose this service serves is to load i18n files of dependencies in advance in the config phase

    angular.module('CCC.Assess').provider('TranslateFileService', function () {

        /*=========== SERVICE PROVIDER API ===========*/

        var TranslateFileService;

        var translateFileList = [];

        var translateFilesGotten = false;

        this.addTranslateFile = function (fileConfig) {

            if (translateFilesGotten) {
                throw new Error('TranslateFileService.cannotAddAfterTranslateFilesRetrieved'); // the getTranslateFile method is usually used to gather all i18n files to start up i18n $translate service, so adding after the fact would be bad
            }

            translateFileList.push(fileConfig);
        };

        this.getTranslateFiles = function () {
            translateFilesGotten = true;
            return translateFileList;
        };

        /*============ SERVICE DECLARATION ============*/

        this.$get = [

            '$state',

            function ($state) {

                /*============ PRIVATE VARIABLES AND METHODS ============*/


                /*============ SERVICE API ============*/

                TranslateFileService = {};


                /*============ LISTENERS ===========*/

                /*============ INITIALIZATION ============*/


                /*============ SERVICE PASSBACK ============*/

                return TranslateFileService;
            }
        ];
    });

})();
