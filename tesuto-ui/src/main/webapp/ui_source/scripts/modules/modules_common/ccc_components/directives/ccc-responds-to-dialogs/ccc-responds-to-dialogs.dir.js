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
     * All dialogs or elements with role of dialog need to trigger the 'ccc.dialog.onBeforeOpen' and 'ccc.dialog.closed' events
     * It should fire the 'ccc.dialog.onBeforeOpen' event before it is rendered into the dom
     * This directive is placed elements that should be hidden when a dialog shows up
     * these events fire to make sure that the user cannot navigate away from the dialog content
     */

    angular.module('CCC.Components').directive('cccRespondsToDialogs', function () {

        return {

            restrict: 'A',

            controller: [

                '$scope',
                '$element',

                function ($scope, $element) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var numberOfTimesHidden = 0;


                    /*============ LISTENERS ============*/

                    // when a dialog opens, we need to put aria-hidden on this element and keep track of how many times hidden
                    $scope.$on('ccc.dialog.onBeforeOpen', function () {
                        numberOfTimesHidden++;
                        $element.attr('aria-hidden', 'true');
                    });

                    // when a dialog closes, we need to put aria-hidden to false if the counter hits 0
                    $scope.$on('ccc.dialog.closed', function () {
                        numberOfTimesHidden--;
                        if (numberOfTimesHidden <= 0) {

                            numberOfTimesHidden = 0;

                            // this is no longer hidden and is the focus
                            $element.attr('aria-hidden', 'false');
                        }
                    });


                    /*============ INITIALIZATION ============*/
                }
            ]

        };

    });

})();

