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

    angular.module('CCC.View.Home').directive('cccRecentActivations', function () {

        return {

            restrict: 'E',

            scope: {

            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ MODEL ============*/

                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<div class="activity" id="1447301427">',
                    '<div>',
                        '<strong class="value student">Returning, Regina R.</strong>',
                    '</div>',
                    '<div class="details">',
                        '<strong class="activated text-success">Activated</strong><strong class="deactivated text-danger hide">Deactivated</strong> by <em class="value proctor">Polly Proctor</em> on <span class="value timestamp">11 Nov 2015 8:15</span> for <span class="value location">Test Center C</span>',
                    '</div>',
                    '<div class="assessments">',
                        '<span class="value assessments-list"><span>English Placement,</span> <span>Math Placement,</span> </span>',
                    '</div>',
                    '<div class="accommodations hide">',
                        '<ul class="value accommodations-list"></ul>',
                    '</div>',
                    '<div class="actions hide">',
                        '<button class="btn btn-xs btn-block btn-link deactivate"><span class="fa fa-times"></span> Deactivate</button>',
                    '</div>',
                '</div>'

            ].join('')
        };
    });

})();
