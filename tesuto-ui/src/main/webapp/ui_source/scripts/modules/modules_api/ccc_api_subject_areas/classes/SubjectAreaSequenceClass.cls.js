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

    angular.module('CCC.API.SubjectAreas').factory('SubjectAreaSequenceClass', [

        '$q',
        'Moment',
        '$translate',
        'Cloneable',
        'SubjectAreaCourseClass',
        'SubjectAreasAPIService',

        function ($q, Moment, $translate, Cloneable, SubjectAreaCourseClass, SubjectAreasAPIService) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var DEFAULT_EXPLANATION = 'Please see a counselor for your placement.';


            /*============ CLASS DECLARATION ============*/

            var SubjectAreaSequenceClass = function (configs_in) {

                var defaults = {
                    disciplineId: null,
                    cb21Code: null,
                    level: null,
                    courses: [],
                    explanation: '',
                    mappingLevel: '',
                    showStudent: false
                };

                // merge in the defaults onto the instance
                var that = this;
                // extend from the Cloneable class
                Cloneable.call(that);
                $.extend(true, that, defaults, configs_in || {});

                // coerce some values


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                // generate useful meta data from initial config data (from server etc...)
                var initialize = function () {

                    // let's attach a display friendly title that sums up the level and cb21Code
                    that.title = that.cb21Code + ' - ' + that.level;

                    // make sure we have a default explanation
                    if (!that.explanation) {
                        that.explanation = DEFAULT_EXPLANATION;
                    }

                    // coerce any courses that have not been coerced... say that five times fast
                    for(var i=0; i < that.courses.length; i++) {
                        if (!(that.courses[i] instanceof SubjectAreaCourseClass)) {

                            // transfer a couple properties to the course so it can be self suffcient for CRUD operations
                            that.courses[i]['disciplineId'] = that.disciplineId;
                            that.courses[i]['cb21Code'] = that.cb21Code;

                            that.courses[i] = new SubjectAreaCourseClass(that.courses[i]);
                        }
                    }
                };


                /*=============== PUBLIC PROPERTIES =============*/


                /*=============== PUBLIC METHODS =============*/

                // method used to send data for creation and update
                that.serialize = function () {

                    // this is the format the backend needs for creation and updating
                    var serializeModel = {
                        explanation: that.explanation ? that.explanation : DEFAULT_EXPLANATION,
                        showStudent: that.showStudent,
                        mappingLevel: that.mappingLevel
                    };

                    return serializeModel;
                };

                that.create = function () {
                    return SubjectAreasAPIService.subjectAreaSequences.create(that.serialize());
                };

                that.update = function () {
                    return SubjectAreasAPIService.subjectAreaSequences.update(that.disciplineId, that.cb21Code, that.courseGroup, that.serialize());
                };


                /*=============== INITIALIZTION =============*/

                initialize();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return SubjectAreaSequenceClass;
        }
    ]);

})();
