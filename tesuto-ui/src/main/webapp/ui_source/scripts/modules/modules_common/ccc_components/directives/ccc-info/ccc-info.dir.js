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
     *  ccc-info uses multiple transclusions with ng-transclude.
     *
     *  To use this directive call from the parent directive with a nested <strong> and <span> tag
     *
     *  <ccc-info>
     *      <strong> One sentence to drive home the information for the user.</strong>
     *      <span> Outline the steps the user should take, or further explanation goes here.</span>
     *  </ccc-info>
     *
     *  see resource for further explanation:
     *
     *  http://blog.thoughtram.io/angular/2015/11/16/multiple-transclusion-and-named-slots.html
     *
     */
    angular.module('CCC.Components').directive('cccInfo', function () {

        return {

            restrict: 'E',

            transclude: {
                'strong-slot' : 'strong',
                'info-slot': 'span'
            },

            controller: [

                function () {

                    /*============ MODEL =============*/

                    /*============ BEHAVIOR ============*/
                }
            ],

            template: [

                '<div class="alert alert-info info-body-with-icon">',
                    '<span class="icon fa fa-info-circle" role="presentation" aria-hidden="true"></span>',
                    '<div class="info-body">',
                        '<strong ng-transclude="strong-slot"></strong>',
                        '<span ng-transclude="info-slot"></span>',
                    '</div>',
                '<div>'
            ].join('')

        };

    });

})();
