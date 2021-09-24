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
     * USE THIS DIRECTIVE IF YOU WANT THE WRAPPER DIRECTIVES FOR BACKGROUND CONTENT, MODAL, AND COLOR OVERLAY
     * IF YOU JUST WANT THE ASSESSMENT PLAYER USE ccc-asmt-player directive only
     */

    angular.module('CCC.AsmtPlayer').directive('cccAsmtPlayerView', function () {

        return {

            restrict: 'E',

            scope: {
                taskSetService: '=',    // must be provided and have methods for getting tasks and submitting tasks
                assessment: '=',        // will contain the title and other assessment configurations
                initialTaskSet: '=?',   // you can provide an initial task set to start things off if you like
                user: '=',              // the user using this assessment
                allowPause: '@'         // is this a pausable assessment
            },

            controller: [

                '$scope',
                'BackgroundContentService',

                function ($scope, BackgroundContentService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    /*=========== MODEL ===========*/

                    $scope.allowPause = $scope.allowPause === 'true' ? true : false;


                    /*=========== LISTENERS ===========*/

                    $scope.$on('ccc-asmt-player-view.requestDisable', function () {
                        $scope.$broadcast('ccc-asmt-player.requestDisable');
                    });
                    $scope.$on('ccc-asmt-player-view.requestEnable', function () {
                        $scope.$broadcast('ccc-asmt-player.requestEnable');
                    });


                    /*=========== INITIALIZATION ===========*/

                    // start up the background content service
                    // this service gives us the ability to slide the main view off to the side to show additional content
                    BackgroundContentService.initialize();

                }
            ],

            template: [

                // this is the container for background content that shows when the foreground slides to the left or the right
                '<ccc-background></ccc-background>',

                // the foreground main content
                '<div class="ccc-full-view-container ccc-foreground ccc-bg ccc-fg" ccc-responds-to-dialogs>',

                    '<div class="ccc-full-view-content-container">',
                        '<ccc-asmt-player task-set-service="taskSetService" assessment="assessment" user="user" initial-task-set="initialTaskSet" allow-pause="allowPause"></ccc-asmt-player>',
                    '</div>',

                '</div>',

                // a container for overlays that will always be on top of the main content and background
                '<div class="ccc-full-view-overlay-container">',
                    '<ccc-modal></ccc-modal>',

                    // for accessibility we provide a color overlay. It will be on top but will not trap any mouse events
                    '<ccc-color-overlay></ccc-color-overlay>',
                '</div>'

            ].join('')

        };

    });

})();
