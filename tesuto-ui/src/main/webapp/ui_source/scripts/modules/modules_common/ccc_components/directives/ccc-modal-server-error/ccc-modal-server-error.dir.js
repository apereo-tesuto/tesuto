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

    angular.module('CCC.Components').directive('cccModalServerError', function () {

        return {

            restrict: 'E',

            // you would typically want to pass in the modal instance so you can use it to self close etc..
            scope: {
                modal: "=",
                err: "="
            },

            controller: [

                '$scope',
                '$element',
                '$location',

                function ($scope, $element, $location) {

                    /*============ MODEL =============*/

                    $scope.showErrorDetails = $location.search()['dev'] === 'true';


                    /*============ BEHAVIOR ============*/

                    $scope.stay = function () {
                        $scope.modal.instance.dismiss();
                    };

                    $scope.continue = function () {
                        $scope.modal.instance.close();
                    };
                }
            ],

            template: [

                // NOTE: [autofocus] for modals
                '<div autofocus tabindex="-1" id="{{modal.labelId}}">',

                    '<div class="modal-header">',

                        '<h3 class="modal-title text-warning">',
                            '<i class="fa fa fa-exclamation-triangle text-symbol-warning" aria-hidden="true"></i>',
                            ' <span translate="CCC_COMP.MODALS.SERVER_ERROR.TITLE"></span>',
                        '</h3>',

                    '</div>',

                    '<div class="modal-content-wrapper">',
                        // NOTE: Here we add in the contentID
                        '<div class="modal-body" id="{{modal.contentId}}">',
                            '<p translate="CCC_COMP.MODALS.SERVER_ERROR.BODY"></p>',
                            '<ccc-error-details err="err" ng-if="showErrorDetails"></ccc-error-details>',
                        '</div>',
                    '</div>',

                '</div>',

                '<div class="modal-footer">',
                    '<button tabindex="0" class="btn btn-default" ng-click="continue()">',
                        '<span translate="CCC_COMP.MODALS.BUTTONS.OKAY"></span> ',
                        '<i class="fa fa-chevron-right"></i>',
                    '</button>',
                '</div>'
            ].join('')

        };

    });

})();
