/**
 * This script will scan for all locale-*.json files and export a csv file for others to be able to scan
 * Run "node exportInternationalizedCopy.js" from the directory this file lives in
 */

(function () {

    /*============ DEPENDENCIES ============*/

    // from nodeJS
    var fs = require('fs');
    var path = require('path');

    // third party
    var Promise = require('bluebird');
    var fastCSV = require("fast-csv");
    var _ = require('underscore');
    var nodeDir = require('node-dir');


    /*============ CONSTANTS ============*/

    var EXPORT_FILE_PATH = "i18nCOPY.csv";  // the full file path for the export
    var STARTING_DIRECTORY_SEARCH_PATH = __dirname + '/ui_source/scripts/modules/'; // the path to start looking for i18n files

    var LOCALE_FILE_REGEX_MATCH = /locale-(.*)\.json$/i;    // regex to match and extract language on locale files


    /*============ PRIVATE VARIABLES / METHODS ===========*/

    var flattenJSObject = function (obj, includePrototype, into, prefix) {
        into = into || {};
        prefix = prefix || "";

        for (var k in obj) {
            if (includePrototype || obj.hasOwnProperty(k)) {
                var prop = obj[k];
                if (prop && typeof prop === "object" &&
                    !(prop instanceof Date || prop instanceof RegExp)) {
                    flattenJSObject(prop, includePrototype, into, prefix + k + ".");
                }
                else {
                    into[prefix + k] = prop;
                }
            }
        }
        return into;
    };

    var getJSONFromFile = function (pathToJSON) {

        return new Promise(function (resolve, reject) {
            var content = fs.readFileSync(pathToJSON);

            try {
                var newJSONObject = JSON.parse(content.toString());
                resolve(newJSONObject);
            } catch (e) {

                reject(e);
            }
        });
    };

    var getProcessFilePromise = function (file) {

        return new Promise(function (resolve, reject) {

            var filePath = path.relative(__dirname, path.dirname(file));
            var fileName = path.parse(file).base;
            var lang = LOCALE_FILE_REGEX_MATCH.exec(fileName)[1];

            getJSONFromFile(filePath + '/' + fileName).then(function (jsonObject) {
                resolve({
                    path: filePath,
                    lang: lang,
                    fileName: fileName,
                    content: jsonObject
                });
            });
        });
    };

    var getAllInternationalizationFileObjects = function () {

        return new Promise(function (resolve, reject) {

            var processFiles = function (fileList) {

                var allFilePromises = [];
                _.each(fileList, function (file) {
                    allFilePromises.push(getProcessFilePromise(file));
                });

                return Promise.all(allFilePromises);
            };

            // fetch all locale files and send them off for processing
            nodeDir.readFiles(STARTING_DIRECTORY_SEARCH_PATH,
                {
                    match: LOCALE_FILE_REGEX_MATCH,
                    exclude: /locale-none\.json$/
                },

                function (err, content, next) {
                    if (err) {
                        throw err;
                    }
                    next();
                },

                function (err, files) {
                    if (err) throw err;
                    processFiles(files).then(function (processedFiles) {
                        resolve(processedFiles);
                    });
                }
            );

        });
    };

    var groupFilesByDirectory = function (fileObjectList) {
        return _.groupBy(fileObjectList, function(fileObject){ return fileObject.path; });
    };

    var mergePhraseKeySets = function (phraseKeySets) {

        var mergedPhraseKeys = [];
        var mergedPhraseKeysMap = {};

        _.each(phraseKeySets, function (phraseKeySet) {
            // lang: fileObject.lang,
            // filePath: fileObject.path
            // phraseKeyValues: flattenedPhraseKeyValueList
            _.each(phraseKeySet.phraseKeyValues, function (value, prop) {

                if (!mergedPhraseKeysMap[prop]) {
                    var phraseKeyObject = {
                        phraseKey: prop,
                        filePath: phraseKeySet.filePath
                    };
                    mergedPhraseKeysMap[prop] = phraseKeyObject;
                    mergedPhraseKeys.push(phraseKeyObject);
                }

                // add this translation
                mergedPhraseKeysMap[prop][phraseKeySet.lang] = value;
            });
        });

        return mergedPhraseKeys;
    };

    // this method will get access to all files that cover a set of phraseKeys in different languages
    // it will then generate an array of all possible key phrases with every language translation available
    var processInternationalizationFileGroup = function (internationalizationFileObjectsGroup) {

        var phraseKeySets = [];
        _.each(internationalizationFileObjectsGroup, function (fileObject) {

            var flattenedPhraseKeyValueList = flattenJSObject(fileObject.content);
            phraseKeySets.push({
                lang: fileObject.lang,
                filePath: fileObject.path,
                phraseKeyValues: flattenedPhraseKeyValueList
            });
        });

        return mergePhraseKeySets(phraseKeySets);
    };

    var generateInternationalizationCSV = function (dataRows) {

        return new Promise(function (resolve, reject) {

            // add the header row (as we add languages, this will need to be updated)
            dataRows.unshift({
                phraseKey: 'Phrase Key',
                filePath: 'File Path',
                en: 'English (en)',
                es: 'Spanish (es)'
            });

            var csvStream = fastCSV.createWriteStream({
                headers: false,
                rowDelimiter: '\n',
            }).transform(function(row){

                return { // NOTE: you can comment out everything but the en row in order to export just the phrases for spell checking etc...
                    phraseKey: row.phraseKey,
                    en: row.en,
                    es: row.es,
                    filePath: row.filePath
                };
            });

            var writableStream = fs.createWriteStream(EXPORT_FILE_PATH).on("finish", function(){
                resolve(EXPORT_FILE_PATH);
            });

            csvStream.pipe(writableStream);

            _.each(dataRows, function (dataRow) {
                csvStream.write(dataRow);
            });

            csvStream.end();
        });
    };

    var formatProcessedGroupsData = function (processedGroups) {

        // flatten all the data generate CSV file for all groups
        var flattenedList = [];
        _.each(processedGroups, function (processedGroup) {
            flattenedList = flattenedList.concat(processedGroup);
        });

        return flattenedList;
    };


    /*============= MAIN SCRIPT METHOD ===========*/

    var exportInternationalizedCopy = function () {

        // scan for all filenames that match the locale-*.json format
        getAllInternationalizationFileObjects().then(function (allInternationalizationFileObjects) {

            // group them by directory name
            var groupedInternationalizationFiles = groupFilesByDirectory(allInternationalizationFileObjects);

            // run each group through to get all phrase values across all languages in proper array format
            var processedGroups = [];
            _.each(groupedInternationalizationFiles, function (internationalizationFileGroup) {
                processedFileGroup = processInternationalizationFileGroup(internationalizationFileGroup);
                processedGroups.push(processedFileGroup);
            });

            // minimally we need to flatten the groups into one array to turn it into rows for CSV processing
            var formattedProcessedGroupsData = formatProcessedGroupsData(processedGroups);

            // genereate the CSV List
            generateInternationalizationCSV(formattedProcessedGroupsData).then(function (exportedCSVFilePath) {
                console.log("DONE! Exported at: ", exportedCSVFilePath);
            });

        }, function (err) {

            console.log("UH OH! An error occured: ", e, e.stack);
        });
    };


    /*============ INITIALIZATION ============*/

    exportInternationalizedCopy();

})();