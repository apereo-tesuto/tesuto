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

    angular.module('CCC.API.SubjectAreas').factory('SubjectAreaClass', [

        '$q',
        'Moment',
        '$translate',
        'Cloneable',
        'CCCUtils',
        'SubjectAreasAPIService',

        function ($q, Moment, $translate, Cloneable, CCCUtils, SubjectAreasAPIService) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var SubjectAreaClass = function (configs_in) {

                var defaults = {

                    disciplineId: null,
                    collegeId: null,
                    title: '',
                    description: '',
                    sisCode: '',
                    usePrereqPlacementMethod: null,
                    archived: false,
                    sequences: [],
                    noPlacementMessage: '',

                    published: null,
                    publishedDate: null,
                    publishedTitle: null,
                    version: null,
                    lastEditedDate: null,
                    dirty: null,

                    competencyAttributes: {
                        optInMultiMeasure: null,
                        competencyCode: null, // used to be competencyMapDiscipline
                        placementComponentAssess: true,
                        placementComponentMmap: false // force user to make selection
                    }
                };

                // merge in the defaults onto the instance
                var that = this;
                // extend from the Cloneable class
                Cloneable.call(that);
                $.extend(true, that, defaults, configs_in || {});

                // coerce some values


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                // generate useful meta data from initial config data (from server etc...)
                var initialize = function () {};


                /*=============== PUBLIC PROPERTIES =============*/


                /*=============== PUBLIC METHODS =============*/

                // method used to send data for creation and update
                that.serialize = function () {

                    // this is the format the backend needs for creation and updating
                    var serializeModel = {
                        title: that.title,
                        sisCode: that.sisCode,
                        usePrereqPlacementMethod: that.usePrereqPlacementMethod,
                        archived: that.archived,
                        description: that.description,
                        collegeId: that.collegeId,
                        competencyMapDiscipline: that.competencyAttributes.competencyCode,
                        competencyAttributes: that.competencyAttributes,
                        noPlacementMessage: that.noPlacementMessage
                    };

                    return serializeModel;
                };

                that.publish = function () {
                    return SubjectAreasAPIService.subjectAreas.publish(that.disciplineId).then(function (newSubjectArea) {
                        return CCCUtils.coerce('SubjectAreaClass', newSubjectArea);
                    });
                };

                that.create = function () {

                    return SubjectAreasAPIService.subjectAreas.create(that.serialize()).then(function (subjectAreaId) {

                        // once created merge on properties created server side during the create process
                        that.disciplineId = subjectAreaId;
                    });
                };

                that.update = function () {
                    return SubjectAreasAPIService.subjectAreas.update(that.disciplineId, that.serialize());
                };

                that.delete = function () {
                    return SubjectAreasAPIService.subjectAreas.delete(that.disciplineId);
                };

                /**
                 * Subject areas may not come complete with sequences so we may need to fetch them from the server and attach them to the subject area instance
                 * @return {promise} resolves to the list of sequences
                 */
                that.fetchSequences = function () {

                    return SubjectAreasAPIService.subjectAreas.getSequences(that.disciplineId).then(function (sequences) {

                        that.sequences = _.sortBy(sequences, function (sequence) {
                            return sequence.level;
                        });

                        return that.sequences;
                    });
                };

                /**
                 * [deleteCourse description]
                 * @param  {SubjectAreaCourseClass} subjectAreaCourseClassInstance the course to remove from the subject area. Will be removed by courseId
                 * @param  {boolean} persistDelete                 (optional) pass in true to have the course delete itself from the server
                 * @return {promise}                               promsie will resolve to true once all delete operations are successful, rejected otherwise
                 */
                that.deleteCourse = function (subjectAreaCourseClassInstance, persistDelete) {

                    // if you want to force the course to delete itself before removing it from the subject area
                    if (persistDelete) {

                        return subjectAreaCourseClassInstance.delete();

                    // sometimes the course would have already been deleted so just remove it from the list
                    } else {
                        return $q.when(true);
                    }
                };

                /**
                 * Helper method to scan sequences and generate an array of sequence data
                 * @return {Array} array of sequence data for each sequence. Most often used to generate a dropdown selector for sequences
                 */
                that.getTransferLevels = function () {

                    var sortedSequences = _.sortBy(that.sequences, function (sequence) {
                        return sequence.level;
                    });

                    return _.map(sortedSequences, function (sequence) {

                        return {
                            id: sequence.cb21Code,
                            title: sequence.title
                        };
                    });
                };


                /*=============== INITIALIZTION =============*/

                initialize();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return SubjectAreaClass;
        }
    ]);

})();
