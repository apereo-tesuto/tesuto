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

    angular.module('CCC.View.RemoteProctor').directive('cccRemoteProctorAgreement', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES ============*/

                    /*============ MODEL ============*/

                    $scope.termsAccepted = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.acceptTerms = function () {
                        window.location.href = window.location.href + "&acknowledge=true";
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ===========*/

                }
            ],

            template: [

                '<div class="form-container">',
                    '<div class="form-body">',
                        '<h2 translate="CCC_VIEW_REMOTE_PROCTOR.CCC-REMOTE-PROCTOR-AGREEMENT.AGREEMENT_TITLE"></h2>',
                        '<ul>',
                            '<span translate="CCC_VIEW_REMOTE_PROCTOR.CCC-REMOTE-PROCTOR-AGREEMENT.TERMS"></span>',
                        '</ul>',
                        '<div class="alert alert-warning alert-with-icon">',
                            '<span class="icon fa fa-warning" role="presentation" aria-hidden="true"></span>',
                            '<p class="alert-body" translate="CCC_VIEW_REMOTE_PROCTOR.CCC-REMOTE-PROCTOR-AGREEMENT.WARNING"></p>',
                        '</div>',
                        '<h2 translate="CCC_VIEW_REMOTE_PROCTOR.CCC-REMOTE-PROCTOR-AGREEMENT.INSTRUCTIONS_TITLE"></h2>',
                        '<ul>',
                            '<span translate="CCC_VIEW_REMOTE_PROCTOR.CCC-REMOTE-PROCTOR-AGREEMENT.INSTRUCTIONS"></span>',
                        '</ul>',
                        '<form>',
                            '<p><strong translate="CCC_VIEW_REMOTE_PROCTOR.CCC-REMOTE-PROCTOR-AGREEMENT.MUST_AGREE"></strong></p>',
                            '<div class="well well-sm">',
                                '<div class="checkbox">',
                                    '<label>',
                                        '<input type="checkbox" ng-model="termsAccepted"> <span translate="CCC_VIEW_REMOTE_PROCTOR.CCC-REMOTE-PROCTOR-AGREEMENT.I_AGREE"></span>',
                                    '</label>',
                                '</div>',
                            '</div>',
                        '</form>',
                    '</div>',
                    '<div class="form-actions">',
                        '<div class="actions">',
                            '<button ng-click="acceptTerms()" class="btn btn-primary" ng-disabled="!termsAccepted" translate="CCC_VIEW_REMOTE_PROCTOR.CCC-REMOTE-PROCTOR-AGREEMENT.CONTINUE"></button>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
