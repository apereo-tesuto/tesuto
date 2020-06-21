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

    /**
     * DIRECTIVE FOR A "STANDARD" VIEW HEADER LOGO
     */

    angular.module('CCC.View.Layout').directive('cccViewCommonHeaderLogo', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$state',

                function ($scope, $state) {

                    $scope.homeClicked = function (e) {

                        // for mouse blur so when they mouse out the scale goes away
                        if (e.which >= 1 && e.which <= 3) {
                            $(e.currentTarget).blur();
                        }

                        $state.go('home', {}, {reload: true});
                    };
                }
            ],

            template: [
                '<div class="navbar-header">',
                    '<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#ccc-home-navigation">',
                        '<span class="sr-only">Toggle navigation</span>',
                        '<i class="fa fa-bars" aria-hidden="true"></i>',
                    '</button>',
                    '<a id="ccc-header-nav-logo" class="navbar-brand logo" href="#" ng-click="homeClicked($event)">',
                        '<span class="navbar-brand-content">',
                            '<span class="sr-only">Home Button, </span>',
                            '<img class="logo-mark" src="ui/resources/images/multiple_measures_white.png" alt="Tesuto Logo">',
                        '</span>',
                    '</a>',
                '</div>'
            ].join('')

        };

    });

})();
