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

    angular.module('CCC.AsmtPlayer').directive('cccAsmtTaskScoreModal', function () {

        return {

            restrict: 'E',

            // you would typically want to pass in the modal instance so you can use it to self close etc..
            scope: {
                modal: "=",
                score: "="
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ===========*/

                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.cancel = function () {
                        $scope.modal.instance.dismiss();
                    };

                    $scope.deactivate = function () {
                        $scope.modal.instance.close();
                    };


                    /*============ LISTENERS ===========*/

                    /*============ INITIALIZATION ===========*/

                }
            ],

            template: [

                // NOTE: the [autofocus] to ensure proper focus trapping
                '<div autofocus tabindex="-1" id="{{modal.labelId}}">',

                    '<div class="modal-header">',
                        '<h3 class="modal-title">Score For Current Item</h3>',
                    '</div>',

                    '<div class="modal-content-wrapper">',
                        // NOTE: Here we add in the contentID
                        '<div class="modal-body" id="{{modal.contentId}}">',
                            '<div class="score">',
                                '<h3>Your Score: {{score}}</h3>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>',

                '<div class="modal-footer">',
                    '<button tabindex="0" class="btn btn-default" ng-click="cancel()">',
                        '<span translate="Cancel"></span>',
                    '</button>',
                '</div>'

            ].join('')

        };

    });

})();
