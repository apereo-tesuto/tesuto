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

    angular.module('CCC.View.Home').directive('cccUserDetailsForm', function () {

        return {

            restrict: 'E',

            scope: {
                user: '=',
                submitted: '=',
                loading: '='
            },

            template: [

                '<div class="ng-form" name="userDetailsForm">',

                    '<div class="row">',

                        '<div class="col-sm-4">',
                            '<div class="form-group">',

                                '<label for="firstName"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> First Name</label>',
                                '<div ccc-show-errors="userDetailsForm.firstName.$dirty || submitted">',
                                    '<input type="text" ccc-autofocus id="firstName" class="form-control" name="firstName" placeholder="First Name" ',
                                        'required ',
                                        'ng-maxlength="40" ',
                                        'ng-pattern="/^[A-z|0-9|\\.|\\-|\\`|\\,|\\\'|\\s]+$/i" ',
                                        'autocomplete="off" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="user.firstName" ',
                                        'ccc-validation-badge="userDetailsForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="firstNameErrors" ',
                                    '> ',
                                    '<div id="firstNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-first-name" ng-messages="userDetailsForm.firstName.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.FIRST_NAME_REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.FIRST_NAME_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.FIRST_NAME_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                        '<div class="col-sm-3">',
                            '<div class="form-group">',

                                '<label for="middleInitial">Middle Initial</label>',
                                '<div ccc-show-errors="userDetailsForm.middleInitial.$dirty || submitted">',
                                    '<input type="text" id="middleInitial" class="form-control" name="middleInitial" placeholder="" ',
                                        'autocomplete="off" ',
                                        'ng-maxlength="1" ',
                                        'ng-pattern="/^[A-z]+$/i" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="user.middleInitial" ',
                                        'ccc-validation-badge="userDetailsForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="cccMiddleInitialErrors" ',
                                    '> ',
                                    '<div id="cccMiddleInitialErrors" class="ccc-validation-messages noanim ccc-validation-messages-middle-initial" ng-messages="userDetailsForm.middleInitial.$error">',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.MIDDLE_INITIAL_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.MIDDLE_INITIAL_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                        '<div class="col-sm-5">',
                            '<div class="form-group">',

                                '<label for="lastName"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> Last Name</label>',
                                '<div ccc-show-errors="userDetailsForm.lastName.$dirty || submitted">',
                                    '<input type="text" id="lastName" class="form-control" name="lastName" placeholder="Last Name" ',
                                        'required ',
                                        'ng-maxlength="60" ',
                                        'ng-pattern="/^[A-z|0-9|\\.|\\-|\\`|\\,|\\\'|\\s]+$/i" ',
                                        'autocomplete="off" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="user.lastName" ',
                                        'ccc-validation-badge="userDetailsForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="cccLastNameErrors" ',
                                    '> ',
                                    '<div id="cccLastNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-last-name" ng-messages="userDetailsForm.lastName.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.LAST_NAME_REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.LAST_NAME_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.FIRST_NAME_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                    '</div>',


                    '<div class="row">',

                        '<div class="col-sm-5">',
                            '<div class="form-group">',

                                '<label for="email"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> Email</label>',
                                '<div ccc-show-errors="userDetailsForm.email.$dirty || submitted">',
                                    '<input type="text" id="email" class="form-control" name="email" placeholder="Email" ',
                                        'required ',
                                        'ng-maxlength="120" ',
                                        'autocomplete="off" ',
                                        'ccc-required-email="allowEmpty" ',
                                        'autocomplete="false" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="user.emailAddress" ',
                                        'ccc-validation-badge="userDetailsForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="cccEmailErrors" ',
                                    '> ',
                                    '<div id="cccEmailErrors" class="ccc-validation-messages noanim ccc-validation-messages-email" ng-messages="userDetailsForm.email.$error">',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.EMAIL_MAX"></span></p>',
                                        '<p ng-message="requiredEmail" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.EMAIL"></span></p>',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.EMAIL"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                        '<div class="col-sm-4">',
                            '<div class="form-group">',

                                '<label for="phoneNumber">Phone</label>',
                                '<div ccc-show-errors="userDetailsForm.phoneNumber.$dirty || submitted">',
                                    '<input type="text" id="phoneNumber" class="form-control" name="phoneNumber" placeholder="Ten Digit Phone" ',
                                        'autocomplete="off" ',
                                        'ui-us-phone-number ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="user.phoneNumber" ',
                                        'ccc-validation-badge="userDetailsForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="cccPhoneNumberErrors" ',
                                        'ng-maxlength="14" ',
                                    '> ',
                                    '<div id="cccPhoneNumberErrors" class="ccc-validation-messages noanim ccc-validation-messages-phone" ng-messages="userDetailsForm.phoneNumber.$error">',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.PHONE_LENGTH"></span></p>',
                                        '<p ng-message="usPhoneNumber" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.PHONE"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                        '<div class="col-sm-3">',
                            '<div class="form-group">',

                                '<label for="extension">Extension</label>',
                                '<div ccc-show-errors="userDetailsForm.extension.$dirty || submitted">',
                                    '<input type="text" id="extension" class="form-control" name="extension" placeholder="Extension" ',
                                        'autocomplete="off" ',
                                        'ng-maxlength="10" ',
                                        'ng-pattern="/^[0-9]+$/i" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="user.extension" ',
                                        'ccc-validation-badge="userDetailsForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="cccExtensionErrors" ',
                                    '> ',
                                    '<div id="cccExtensionErrors" class="ccc-validation-messages noanim ccc-validation-messages-extension" ng-messages="userDetailsForm.extension.$error">',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.EXTENSION_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.EXTENSION_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
