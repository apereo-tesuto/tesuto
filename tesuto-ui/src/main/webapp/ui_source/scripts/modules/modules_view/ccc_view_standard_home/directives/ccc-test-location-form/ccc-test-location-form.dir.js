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

    angular.module('CCC.View.Home').directive('cccTestLocationForm', function () {

        return {

            restrict: 'E',

            // this form requires the parent scope (this scope) to have testLocation

            template: [

                '<div class="ng-form row" name="testLocationForm">',
                    '<div class="col-md-8 col-md-offset-2">',
                    '<ccc-label-required></ccc-label-required>',
                        '<h3 translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.LOCATION.TITLE" ccc-focusable></h3>',

                        '<div class="form-group">',
                            '<div class="radio">',
                                '<label>',
                                    '<input type="radio" ',
                                        'name="locationType" ',
                                        'value="ON_SITE" ',
                                        'ng-model="testLocation.locationType" ',
                                        'ng-checked="testLocation.locationType === \'ON_SITE\'" ',
                                        'ng-disabled="loading" ',
                                    '/>',
                                    '<span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.LOCATION.ON_SITE"></span>',
                                '</label>',
                            '</div>',
                            '<div class="radio">',
                                '<label>',
                                    '<input type="radio" ',
                                        'name="locationType" ',
                                        'value="REMOTE" ',
                                        'ng-model="testLocation.locationType" ',
                                        'ng-checked="testLocation.locationType === \'REMOTE\'" ',
                                        'ng-disabled="loading" ',
                                    '/>',
                                    '<span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.LOCATION.REMOTE"></span>',
                                '</label>',
                            '</div>',
                        '</div>',

                        '<div class="form-group">',
                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i>',
                            '<label for="name" translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.LOCATION.NAME"></label>',
                            '<div ccc-show-errors="testLocationForm.name.$dirty || submitted">',
                                '<input type="text" id="name" class="form-control" name="name" placeholder="Test Location Name" ',
                                    'required ',
                                    'ng-maxlength="120" ',
                                    'autocomplete="off" ',
                                    'ng-model-options="{ debounce: 100 }" ',
                                    'ng-disabled="loading" ',
                                    'ng-model="testLocation.name" ',
                                    'ccc-validation-badge="testLocationForm" ',
                                    'ccc-validation-badge-style="fullWidth" ',
                                    'aria-describedby="cccTestLocationNameErrors" ',
                                '> ',
                                '<div id="cccTestLocationNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-name" ng-messages="testLocationForm.name.$error">',
                                    '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ERROR.NAME_REQUIRED"></span></p>',
                                    '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ERROR.NAME_MAX_LENGTH"></span></p>',
                                '</div>',
                            '</div>',
                        '</div>',

                        '<div class="form-group">',
                            '<label for="addTestLocationFormDistrict" translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.LOCATION.COLLEGE"></label>',

                            '<div>',
                                '<div ccc-dropdown-focus class="btn-group">',
                                    '<button ng-disabled="loading" type="button" class="btn btn-default btn-full-width-when-small dropdown-toggle" ng-class="{\'btn-primary\': listStyle === \'primary\'}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                                        '<i class="fa fa-university" aria-hidden="true"></i>',
                                        '<span ng-if="loading">Loading <span class="fa fa-spinner fa-spin" aria-hidden="true"></span></span>',
                                        '<span ng-if="!loading && collegeList.length === 0">No Colleges Available</span>',
                                        '<span ng-if="!loading && collegeList.length !== 0">{{selectedCollege.name}} <span class="caret" aria-hidden="true"></span></span>',
                                    '</button>',
                                    '<ul class="dropdown-menu">',
                                        '<li ng-repeat="college in collegeList track by college.cccId" class="dont-break-out" ng-class="{selected: college.cccId === selectedCollege.cccId}">',
                                            '<a href="#" ng-click="setTestLocationCollege(college)">{{college.name}}</a>',
                                        '</li>',
                                    '</ul>',
                                '</div>',
                            '</div>',

                        '</div>',

                        '<div class="form-group margin-bottom-double">',
                            '<label for="capacity" translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.LOCATION.CAPACITY"></label>',
                            '<div ccc-show-errors="testLocationForm.capacity.$dirty || submitted">',
                                '<p class="help-block" id="addTestLocationFormCapacityHelpBlock" translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.HELP.CAPACITY"></p>',
                                '<div class="row">',
                                    '<div class="col-xs-4 col-sm-3 col-md-3">',
                                        '<input type="text" id="capacity" class="form-control" name="capacity" ',
                                            'placeholder="0-9999" ',
                                            'ng-maxlength="4" ',
                                            'ng-pattern="/^[0-9]*$/" ',
                                            'autocomplete="off" ',
                                            'ng-model-options="{ debounce: 100 }" ',
                                            'ng-disabled="loading" ',
                                            'ng-model="testLocation.capacity" ',
                                            'ccc-validation-badge="testLocationForm" ',
                                            'ccc-validation-badge-style="fullWidth" ',
                                            'aria-describedby="cccTestLocationCapacityErrors" ',
                                        '> ',
                                    '</div>',
                                '</div>',
                                '<div id="cccTestLocationCapacityErrors" class="ccc-validation-messages noanim ccc-validation-messages-capacity" ng-messages="testLocationForm.capacity.$error">',
                                    '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ERROR.CAPACITY_MAX_LENGTH"></span></p>',
                                    '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ERROR.CAPACITY_PATTERN"></span></p>',
                                '</div>',
                            '</div>',
                        '</div>',

                        '<h3 translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ASSESSMENTS.TITLE"></h3>',
                        '<p id="ccc-assessment-list-description" class="help-block" translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.HELP.ASSESSMENTS"></p>',
                        '<div class="form-group margin-bottom-double">',
                            '<div>',
                                '<ccc-radio-box labelled-by="ccc-assessment-list-description" input-type="checkbox" ng-repeat="radio in assessments | orderBy: [\'radio.name\'] track by radio.id" radio="radio" is-disabled="loading" data-notify="dismiss"></ccc-radio-box>',
                            '</div>',
                        '</div>',

                        '<h3 translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ENABLED.TITLE"></h3>',
                        '<p class="help-block" translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.HELP.ENABLED"></p>',
                        '<div class="radio">',
                            '<label>',
                                '<input type="radio" ',
                                    'name="enabled" ',
                                    'ng-value="true" ',
                                    'ng-model="testLocation.enabled" ',
                                    'ng-checked="testLocation.enabled === true" ',
                                    'ng-disabled="loading" ',
                                '/>',
                                '<span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ENABLED.TITLE"></span>',
                            '</label>',
                        '</div>',
                        '<div class="radio">',
                            '<label>',
                                '<input type="radio" ',
                                    'name="enabled" ',
                                    'ng-value="false" ',
                                    'ng-model="testLocation.enabled" ',
                                    'ng-checked="testLocation.enabled === false" ',
                                    'ng-disabled="loading" ',
                                '/>',
                                '<span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ENABLED.DISABLED"></span>',
                            '</label>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')
        };

    });

})();
