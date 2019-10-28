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

    angular.module('CCC.View.Home').directive('cccHomeHeader', function () {

        return {

            restrict: 'E',

            controller: [

                '$rootScope',
                '$scope',
                '$element',
                '$location',
                '$state',
                'BackgroundContentService',
                'CurrentUserService',
                'ModalService',
                'ViewManagerWatchService',
                'localStorageService',

                function ($rootScope, $scope, $element, $location, $state, BackgroundContentService, CurrentUserService, ModalService, ViewManagerWatchService, localStorageService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.currentUser = CurrentUserService.getUser();

                    $scope.showSettings = $location.search()['dev'] === 'true';
                    $scope.showStudenLookup = CurrentUserService.hasPermission('FIND_STUDENT');

                    $scope.allowPasscodes = CurrentUserService.hasPermission('GENERATE_PRIVATE_PASSCODE_FOR_CURRENT_USER');

                    $scope.currentView = false;

                    $scope.canUserSearchForStudents = CurrentUserService.hasPermission('FIND_STUDENT');


                    /*============ BEHAVIOR ============*/

                    $scope.showOptions = function (event) {

                        BackgroundContentService.openBackgroundContent({
                            id: 'home_options',
                            title: 'CCC_VIEW_HOME.GLOBAL_OPTIONS.TITLE',
                            position: 'right',
                            template: '<ccc-home-options></ccc-home-options>',
                            width: 35,
                            onClose: function () {
                                $(event.target).focus();
                            }
                        });
                    };

                    $scope.showPasscodesModal = function () {
                        var modalScope = $rootScope.$new();

                        ModalService.open({
                            scope: modalScope,
                            template: '<ccc-modal-passcodes modal="modal"></ccc-modal-passcodes>'
                        });
                    };


                    /*============ LISTENERS ============*/

                    // add those back in after the state change
                    $rootScope.$on('ViewManagerWatchService.viewChanged', function (e, newView) {
                        $scope.currentView = newView.id;
                    });

                    $scope.$on('ccc-cccid-search.advancedSearchClicked', function () {
                        localStorageService.set('ccc-student-lookup-mode', 'ADVANCED');
                        $state.go('studentLookup', {cccId: null}, {reload: true});
                    });

                    $scope.$on('ccc-cccid-search.studentFound', function (e, student) {
                        $state.go('studentResult', {student: student});
                    });

                    $scope.$on('ccc-cccid-search.studentNotFound', function (e, cccId) {
                        $state.go('studentLookup', {cccId: cccId}, {reload: true});
                    });


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

                    '<span role="menuitem" class="noanim">',

                        // '<ul class="nav navbar-nav" role="group" class="noanim" ng-if="!canUserSearchForStudents">',
                        //     '<li ui-sref-active="active" role="menuitem"><a href="#" class="app-nav-button" ui-sref="home" ui-sref-opts="{reload:true}" id="ccc-header-proctor-location"><span translate="CCC_VIEW_HOME.HEADER.DASHBOARD"></span></a></li>',
                        // '</ul>',

                        '<ccc-cccid-search ng-if="canUserSearchForStudents && currentView && currentView !==\'proctor-student-search\'" class="visible-lg-inline-block visible-md-inline-block noanim"></ccc-cccid-search>',

                    '</span>',

                    // '<ul class="nav navbar-nav noanim" role="group">',
                    //     '<li ui-sref-active="active" role="menuitem"><a href="#" class="app-nav-button" ui-sref="home" ui-sref-opts="{reload:true}" id="ccc-header-proctor-location"><span translate="CCC_VIEW_HOME.HEADER.DASHBOARD"></span></a></li>',
                    //     '<li ui-sref-active="active" ng-if="showStudenLookup" role="menuitem"><a href="#" class="app-nav-button" ui-sref="studentLookup({cccId: null})" ui-sref-opts="{reload:true}" id="ccc-header-student-lookup"><span translate="CCC_VIEW_HOME.HEADER.STUDENT_LOOKUP"></span></a></li>',
                    // '</ul>',

                    '<ul class="nav navbar-nav navbar-right ccc-user-navigation" role="group">',

                        '<li role="menuitem">',
                            '<a ui-sref="help" target="_blank" class="app-nav-button" id="ccc-help-button" translate="CCC_VIEW_STANDARD_LAYOUT.HEADER.HELP"></a>',
                        '</li>',

                        '<li ccc-dropdown-focus role="menuitem">',
                            '<button class="btn btn-default dropdown-toggle app-nav-button" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                                '{{currentUser.displayName}} ',
                                '<span class="caret"></span>',
                            '</button>',
                            '<ul class="dropdown-menu" aria-labelledby="dropdownMenu1">',
                                '<li><a href="#" id="ccc-header-nav-passcodes" ng-if="allowPasscodes" ng-click="showPasscodesModal()" class="ccc-home-header-passcodes"><span class="fa fa-key" aria-hidden="true"></span> <span translate="CCC_VIEW_STANDARD_LAYOUT.HEADER.PASSCODES"></span></a></li>',
                                '<li ng-if="canUserSearchForStudents" class="visible-xs-inline-block visible-sm-inline-block"><a href="#" ui-sref="studentLookup" ui-sref-opts="{reload: true}" id="ccc-header-nav-student-search"><span class="fa fa-search" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.HEADER.STUDENT_LOOKUP"></span></a></li>',
                                '<li ng-if="showSettings"><a href="#" id="ccc-header-nav-settings" ng-click="showOptions($event)" class="ccc-home-header-settings"><span class="fa fa-cog" aria-hidden="true"></span> <span translate="CCC_VIEW_STANDARD_LAYOUT.HEADER.SETTINGS"></span></a></li>',
                                '<li><a href="#" id="ccc-header-nav-sign-out" ui-sref="logout"><span class="fa fa-sign-out"></span><span translate="CCC_VIEW_STANDARD_LAYOUT.HEADER.SIGN_OUT"></span></a></li>',
                            '</ul>',
                        '</li>',

                    '</ul>',

                '</div>'
            ].join('')

        };

    });

})();


