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

    angular.module('CCC.View.Student').directive('cccStudentOptions', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',

                function ($scope) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    /*=========== MODEL ===========*/

                    /*=========== BEHAVIOR ===========*/

                    /*=========== LISTENERS ===========*/

                    /*=========== INITIALIZATION ===========*/

                }
            ],

            template: [
                '<div class="container-fluid">',
                    '<div class="row">',
                        '<div class="col-xs-12">',

                            '<ccc-options-select-language></ccc-options-select-language>',

                        '</div>',
                    '</div>',
                '</div>'
            ].join('')

        };

    });

})();
