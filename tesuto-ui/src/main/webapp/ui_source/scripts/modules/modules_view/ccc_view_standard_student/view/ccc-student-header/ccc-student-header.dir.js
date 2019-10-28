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

    angular.module('CCC.View.Student').directive('cccStudentHeader', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                '$element',
                '$location',
                'BackgroundContentService',
                'CurrentStudentService',

                function ($scope, $element, $location, BackgroundContentService, CurrentStudentService) {

                    /*============ MODEL ============*/

                    $scope.showSettings = $location.search()['dev'] === 'true';


                    /*============ BEHAVIOR ============*/

                    $scope.currentStudent = CurrentStudentService.getStudent();

                    $scope.showOptions = function (event) {

                        BackgroundContentService.openBackgroundContent({
                            id: 'home_options',
                            title: 'CCC_STUDENT.GLOBAL_OPTIONS.TITLE',
                            position: 'right',
                            template: '<ccc-student-options></ccc-student-options>',
                            width: 35,
                            onClose: function () {
                                $(event.target).focus();
                            }
                        });
                    };


                    /*============ INITIALIZATION ============*/

                    // let's hide the collapsed responsive menu when they select something
                    // this is important when the menu is collapsed on small devices
                    $($element).find('.nav a').on('click', function () {
                        if ($($element).find('.navbar-toggle').css('display') !== 'none') {
                            $($element).find(".navbar-toggle").trigger("click");
                        }
                    });
                }
            ],

            template: [
                '<ccc-view-common-header-logo></ccc-view-common-header-logo>',

                '<div class="collapse navbar-collapse" id="ccc-home-navigation" role="menu">',

                    '<ul class="nav navbar-nav navbar-right ccc-user-navigation" role="group">',

                        '<li class="visible-xs-block">',
                            '<a href="#" class="app-nav-button" id="ccc-header-nav-cccid">',
                                '<span>CCCID:</span> <span class="id">{{::currentStudent.cccId}}</span>',
                            '</a>',
                        '</li>',

                        '<li role="menuitem">',
                            '<a href="#" ui-sref="help" target="_blank" class="app-nav-button" id="ccc-help-button" translate="CCC_VIEW_STANDARD_LAYOUT.HEADER.HELP"></a>',
                        '</li>',

                        '<li ccc-dropdown-focus role="menuitem">',
                            '<button class="btn btn-default dropdown-toggle app-nav-button" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                                '{{currentStudent.displayName}} ',
                                '<span class="caret"></span>',
                            '</button>',
                            '<ul class="dropdown-menu" role="group">',
                                '<li ng-if="showSettings" role="menuitem"><a href="#" id="ccc-header-nav-settings" ng-click="showOptions($event)" class="ccc-home-header-settings"><span class="fa fa-cog"></span> <span translate="CCC_VIEW_STANDARD_LAYOUT.HEADER.SETTINGS"></span></a></li>',
                                '<li role="menuitem"><a href="#" id="ccc-header-nav-sign-out" ui-sref="logout"><span class="fa fa-sign-out"></span><span translate="CCC_VIEW_STANDARD_LAYOUT.HEADER.SIGN_OUT"></span></a></li>',
                            '</ul>',
                        '</li>',

                        '<li class="navbar-text cccid hidden-xs">CCCID: <span class="id">{{::currentStudent.cccId}}</span></li>',

                    '</ul>',
                '</div>'
            ].join('')

        };

    });

})();


