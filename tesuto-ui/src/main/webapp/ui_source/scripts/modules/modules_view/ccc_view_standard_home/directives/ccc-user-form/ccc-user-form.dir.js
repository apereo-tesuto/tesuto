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

    angular.module('CCC.View.Home').directive('cccUserForm', function () {

        return {

            restrict: 'E',

            // this form requires the parent scope (this scope) to have user, submitted, loading

            template: [

                '<div class="row">',
                    '<div class="col-md-8 col-md-offset-2">',

                        '<ccc-label-required></ccc-label-required>',

                        '<div class="row margin-bottom">',
                            '<div class="col-xs-12">',

                                '<h2 class="section-title section-title-large">Personal Details</h2>',
                                '<ccc-user-details-form user="user" submitted="submitted" loading="loading"></ccc-user-details-form>',

                            '</div>',
                        '</div>',

                        '<div class="row margin-bottom">',
                            '<div class="col-xs-12">',

                                '<h2 class="section-title section-title-large">Login Details</h2>',
                                '<ccc-user-login-details-form user="user" submitted="submitted" loading="loading"></ccc-user-login-details-form>',

                            '</div>',
                        '</div>',

                        '<div class="row margin-bottom">',
                            '<div class="col-xs-12">',

                                '<h2 class="section-title section-title-large">Colleges</h2>',
                                '<div class="well" ccc-show-errors="submitted">',
                                    '<label id="ccc-user-create-college-selector-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> Allow access to:</label>',
                                    '<ccc-user-college-selector user="user" is-disabled="loading" loading="loading" labelled-by="ccc-user-create-college-selector-label"></ccc-user-colloge-selector>',
                                '</div>',

                            '</div>',
                        '</div>',

                        '<div class="row margin-bottom">',
                            '<div class="col-xs-12">',

                                '<h2 class="section-title section-title-large">Roles and Permissions</h2>',
                                '<div class="well" ccc-show-errors="submitted">',
                                    '<label id="ccc-user-form-permissions-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.SELECT_PERMISSIONS"></span></label>',
                                    '<ccc-user-permissions-selector is-disabled="loading" user="user" disable-customizations="true" labelled-by="ccc-user-form-permissions-label"></ccc-user-permissions-selector>',
                                '</div>',

                            '</div>',
                        '</div>',

                        // '<div class="row">',
                        //     '<div class="col-xs-12">',

                        //         '<h2 class="section-title section-title-large">Account Options</h2>',
                        //         '<div class="well" ng-class="{\'well-inactive\': !user.temporary}" ccc-show-errors="true">',
                        //             '<ccc-user-temporary-account-selector is-temporary="user.temporary" user="user" is-disabled="loading"></ccc-user-temporary-account-selector>',
                        //         '</div>',

                        //     '</div>',
                        // '</div>',

                    '</div>',
                '</div>'

            ].join('')
        };

    });

})();
