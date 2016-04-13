import { Injectable } from 'angular2/core';
import { Http, Response, Headers, BrowserXhr } from 'angular2/http';

declare var saveAs: any;

@Injectable()
export class ConverterService extends BrowserXhr {

    uploadUrl = "/rest/converter/upload";
    downloadUrl = "/rest/converter/download";

    public uploadDoc(file: File): Promise<any> {
        return new Promise((resolve, reject) => {
            let formData: FormData = new FormData(),
                xhr: XMLHttpRequest = new XMLHttpRequest();

            formData.append("doc", file, file.name);


            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        resolve("success");
                    } else {
                        reject("fail");
                    }
                }
            };

            xhr.open('POST', this.uploadUrl, true);
            xhr.send(formData);
        });
    }

    public downloadScorm(courseName: String, headerLevel: number): Promise<string> {
        return new Promise((resolve, reject) => {
            let xhr: XMLHttpRequest = new XMLHttpRequest();

            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        var filename = "";
                        var disposition = xhr.getResponseHeader('Content-Disposition');
                        if (disposition && disposition.indexOf('attachment') !== -1) {
                            var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                            var matches = filenameRegex.exec(disposition);
                            if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
                        }
                        var mediaType = 'application/zip';
                        var blob = new Blob([xhr.response], { type: mediaType });

                        saveAs(blob, filename);

                        resolve("success");
                    } else {
                        reject("fail");
                    }
                }
            };

            xhr.responseType = 'arraybuffer';
            xhr.open('POST', this.downloadUrl, true);
            xhr.setRequestHeader("Content-Type", "application/json");
            let data = JSON.stringify({ course: courseName, header: "" + headerLevel });
            console.log(data);
            xhr.send(data);
        });
    }

}        