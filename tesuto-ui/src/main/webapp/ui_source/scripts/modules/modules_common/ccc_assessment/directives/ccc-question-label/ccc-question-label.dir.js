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

    angular.module('CCC.Assessment').directive('cccQuestionLabel', function () {

        return {

            restrict: 'E',

            scope: {
                text: "="
            },

            template: [
                '<span  class="ccc-question-label-background">',
                    '<h2 class="ccc-question-label-text" tabindex="-1"><span class="sr-only">question </span> {{text}}</h2>',
                '</span>'
            ].join('')

        };

    });

})();

