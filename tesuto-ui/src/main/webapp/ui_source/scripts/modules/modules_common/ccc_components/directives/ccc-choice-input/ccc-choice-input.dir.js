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

    angular.module('CCC.Components').directive('cccChoiceInput', function () {

        return {

            restrict: 'E',

            scope: {
                text: "=",
                isSelected: "=",
                type: "="
            },

            controller: [

                '$scope',
                '$element',

                function ($scope, $element) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    /*============ MODEL =============*/

                    /*============ MODEL DEPENDENT METHODS =============*/

                    /*============ BEHAVIOR =============*/

                    /*============ LISTENERS =============*/

                    /*============ INITIALIZATION =============*/

                    $($element).attr('tabindex', '-1');

                }
            ],

            // http://www.w3.org/WAI/GL/wiki/Using_grouping_roles_to_identify_related_form_controls
            template: [
                '<span ng-class="{selected:isSelected, checkbox:type == \'checkbox\'}">',
                    '<span  class="ccc-choice-input-background">',
                        '<span class="ccc-choice-input-text ccc-fg">',
                            '{{text}}',
                        '</span>',
                        '<span class="ccc-choice-input-checkbox-container" ng-if="type == \'checkbox\' && isSelected">',
                            '<i class="fa fa-check" aria-hidden="true"></i>',
                        '</span>',
                    '</span>',
                '</span>'
            ].join('')

        };

    });

})();

