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

    angular.module('CCC.AsmtPlayer').directive('cccViewAssessmentComplete', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$element',
                '$stateParams',

                function ($scope, $element, $stateParams) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var goToStudentActivationStatus = function () {

                        var completionURL = window.location.origin + '/student?assessmentSessionId=' + $scope.assessmentSessionId;

                        // if we were opened form somewhere else then change the parent page
                        if (window.opener) {
                            window.opener.location.href = completionURL;
                            window.close();
                        } else {
                            window.location = completionURL;
                        }
                    };


                    /*============ MODEL ===========*/

                    $scope.assessmentSessionId = $stateParams.assessmentSessionId;
                    $scope.assessmentTitle = $stateParams.assessmentTitle;

                    $scope.assessmentTitleData = {ASSESSMENT_NAME: $scope.assessmentTitle};


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.goStudentHome = function () {
                        goToStudentActivationStatus();
                    };

                    $scope.continue = function () {
                        goToStudentActivationStatus();
                    };


                    /*============ LISTENERS ===========*/

                    /*============ INITIALIZATION ===========*/

                    $($element).find('[ccc-autofocus]').focus();

                }
            ],

            template: [

                '<ccc-view-common-header>',
                    '<header class="app-header" role="banner">',
                        '<div class="app-nav">',
                            '<div class="container">',
                                '<div class="row">',
                                    '<div class="col-xs-12">',
                                        '<nav class="navbar navbar-default" role="navigation" id="app-main-nav">',
                                            '<div class="navbar-header">',
                                                '<a class="navbar-brand logo" href="#" id="ccc-header-nav-logo" ng-click="goStudentHome()"><span class="navbar-brand-content"><img class="logo-mark" src="ui/resources/images/multiple_measures_white.png" alt="CCC logo mark"><span class="logo-text">Tesuto</span></span></a>',
                                            '</div>',
                                        '</nav>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</header>',
                '</ccc-view-common-header>',

                '<div class="container ccc-assessment-complete-content-container">',

                    '<div class="row">',

                        '<div class="col-xs-12">',

                            '<div id="ccc-assessment-complete-label">',

                                '<h1 class="ccc-assessment-complete-assessment-title">',
                                    '<i class="fa fa-check-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_ASSESSMENT_PLAYER.COMPLETION.TITLE"></span> ',
                                '</h1>',

                                '<h2 class="reading-width"><span translate="CCC_VIEW_ASSESSMENT_PLAYER.COMPLETION.BODY" translate-values="{{assessmentTitleData}}"></span></h2>',

                            '</div>',

                            '<p>',
                                '<button tabindex="0" class="btn btn-primary btn-submit-button" ccc-autofocus ng-click="continue()" aria-labelledby="ccc-assessment-complete-label">',
                                    '<span translate="CCC_VIEW_ASSESSMENT_PLAYER.COMPLETION.BUTTON"></span>',
                                '</button>',
                            '</p>',

                        '</div>',

                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
