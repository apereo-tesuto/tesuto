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

    angular.module('CCC.View.Home').directive('cccSubjectAreaForm', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                'CompetencyMapSubjectAreaAPIService',
                'MMSubjectAreaClass',

                function ($scope, CompetencyMapSubjectAreaAPIService, MMSubjectAreaClass) {

                    /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

                    /*============ MODEL ============*/

                    $scope.subjectAreasLoading = true;
                    $scope.subjectAreaList = [];

                    $scope.isMMSubjectArea = $scope.subjectArea instanceof MMSubjectAreaClass;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var initialize = function () {

                        CompetencyMapSubjectAreaAPIService.getSubjectAreas().then(function (subjectAreaList) {

                            $scope.subjectAreaList = subjectAreaList;

                            $scope.$emit('ccc-subject-area-form.loaded');

                            $scope.$broadcast('ccc-item-dropdown.setItems', $scope.subjectAreaList, 'competencyMapSubjectAreaDropdown');

                        }).finally(function () {

                            $scope.subjectAreasLoading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.getCompetencyMapSubjectAreaId = function (item) {
                        return item.disciplineName;
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-item-dropdown.itemSelected', function (e, dropdownId, itemId, item) {
                        if (dropdownId === 'competencyMapSubjectAreaDropdown') {
                            $scope.subjectArea.competencyAttributes.competencyCode = item.disciplineName;
                        }
                    });


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<ng-form name="cccSubjectAreaForm">',

                    '<div class="form-group">',
                        '<label for="title"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.NAME.TITLE"></span></label>',
                        '<p class="help-block" translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.NAME.HELP_TEXT"></p>',
                        '<div ccc-show-errors="cccSubjectAreaForm.title.$dirty || submitted">',
                            '<input type="text" id="title" class="form-control" name="title" placeholder="{{\'CCC_VIEW_HOME.SUBJECT_AREA_FORM.NAME.PLACEHOLDER\' | translate}}" ',
                                'required ',
                                'ng-maxlength="120" ',
                                'ng-model-options="{ debounce: 100 }" ',
                                'ng-disabled="loading || subjectAreasLoading" ',
                                'ng-model="subjectArea.title" ',
                                'ccc-validation-badge="cccSubjectAreaForm" ',
                                'ccc-validation-badge-style="fullWidth" ',
                                'aria-describedby="titleErrors" ',
                                'autocomplete="off" ',
                            '> ',
                            '<div id="titleErrors" class="ccc-validation-messages noanim" ng-messages="cccSubjectAreaForm.title.$error">',
                                '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.NAME.ERROR.REQUIRED"></span></p>',
                                '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.NAME.ERROR.MAX"></span></p>',
                            '</div>',
                        '</div>',
                    '</div>',

                    '<div class="form-group">',
                        '<label for="competencyMapSubjectAreaDropdown">',
                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.COMPETENCIES.TITLE"></span>',
                            ' <i class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="subjectAreasLoading"></i>',
                        '</label>',
                        '<p class="help-block" translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.COMPETENCIES.HELP_TEXT"></p>',
                        '<div ccc-show-errors="cccSubjectAreaForm.competencyMapSubjectAreaDropdown.$dirty || submitted">',

                            '<ccc-item-dropdown ',
                                'id="competencyMapSubjectAreaDropdown" ',
                                'class="btn-full-width" ',
                                'is-required="true" ',
                                'loading="loading || subjectAreasLoading" ',
                                'is-disabled="loading || subjectAreasLoading || subjectArea.disciplineId !== null" ',
                                'required-error-msg="CCC_VIEW_HOME.SUBJECT_AREA_FORM.COMPETENCIES.ERROR.REQUIRED" ',
                                'placeholder="CCC_VIEW_HOME.SUBJECT_AREA_FORM.COMPETENCIES.PLACEHOLDER" ',
                                'initial-items="subjectAreaList" ',
                                'initial-item-id="subjectArea.competencyAttributes.competencyCode" ',
                                'get-item-id="getCompetencyMapSubjectAreaId" ',
                                'get-item-name="getCompetencyMapSubjectAreaId" ',
                            '></ccc-item-dropdown>',

                        '</div>',
                    '</div>',

                    '<div class="form-group">',

                        '<label for="sisCode" translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.SIS_CODE.TITLE" ng-if="::!isMMSubjectArea"></label>',
                        '<p class="help-block" translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.SIS_CODE.HELP_TEXT" ng-if="::!isMMSubjectArea"></p>',

                        '<label for="sisCode" translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.SIS_CODE.TITLE_MM" ng-if="::isMMSubjectArea"></label>',
                        '<p class="help-block" translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.SIS_CODE.HELP_TEXT_MM" ng-if="::isMMSubjectArea"></p>',

                        '<div ccc-show-errors="cccSubjectAreaForm.sisCode.$dirty || submitted">',
                            '<input type="text" id="sisCode" class="form-control" name="sisCode" placeholder="{{ (isMMSubjectArea ? \'CCC_VIEW_HOME.SUBJECT_AREA_FORM.SIS_CODE.PLACEHOLDER_MM\' : \'CCC_VIEW_HOME.SUBJECT_AREA_FORM.SIS_CODE.PLACEHOLDER\') | translate }}" ',
                                'ng-model-options="{ debounce: 100 }" ',
                                'ng-disabled="loading || subjectAreasLoading" ',
                                'ng-model="subjectArea.sisCode" ',
                                'ng-maxlength="120" ',
                                'ccc-validation-badge="cccSubjectAreaForm" ',
                                'ccc-validation-badge-style="fullWidth" ',
                                'aria-describedby="sisCodeErrors" ',
                            '> ',
                            '<div id="sisCodeErrors" class="ccc-validation-messages no-anim" ng-messages="cccSubjectAreaForm.sisCode.$error">',
                                '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.SIS_CODE.ERROR.MAX_LENGTH"></span></p>',
                            '</div>',
                        '</div>',
                    '</div>',

                    '<div class="form-group" ng-if="::!isMMSubjectArea">',
                        '<label><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.PLACEMENT_METHOD.TITLE"></span></label>',
                        '<p class="help-block" translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.PLACEMENT_METHOD.HELP_TEXT"></p>',

                        '<div ccc-show-errors="cccSubjectAreaForm.method.$dirty || submitted">',
                            '<div class="panel panel-default panel-body" ng-class="{\'clean\': subjectArea.usePrereqPlacementMethod !== null}">',
                                '<fieldset>',
                                    '<legend class="sr-only" translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.PLACEMENT_METHOD.HELP_TEXT"></legend>',
                                    '<div class="radio">',
                                        '<label>',
                                            '<input type="radio" value="false" ',
                                                'name="method" ',
                                                'required ',
                                                'ng-value="false" ',
                                                'ng-model="subjectArea.usePrereqPlacementMethod" ',
                                                'ng-checked="subjectArea.usePrereqPlacementMethod === false" ',
                                                'ng-disabled="loading || subjectAreasLoading" ',
                                            '/>',
                                            '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.PLACEMENT_METHOD.SLO"></span>',
                                        '</label>',
                                    '</div>',
                                    '<div class="radio">',
                                        '<label>',
                                            '<input type="radio" value="true" ',
                                                'name="method" ',
                                                'required ',
                                                'ng-value="true" ',
                                                'ng-model="subjectArea.usePrereqPlacementMethod" ',
                                                'ng-checked="subjectArea.usePrereqPlacementMethod === true" ',
                                                'ng-disabled="loading || subjectAreasLoading" ',
                                            '/>',
                                            '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.PLACEMENT_METHOD.PRE"></span>',
                                        '</label>',
                                    '</div>',
                                '</fieldset>',
                            '</div>',
                            '<div id="methodErrors" class="ccc-validation-messages" ng-messages="cccSubjectAreaForm.method.$error">',
                                '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_FORM.PLACEMENT_METHOD.ERROR.REQUIRED"></span></p>',
                            '</div>',
                        '</div>',
                    '</div>',

                '</ng-form>'

            ].join('')
        };

    });

})();
