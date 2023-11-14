import axios from 'axios'

export default class NodesetRestResource {

  constructor() {
    this.baseURL = `${window.location.protocol}//${window.location.hostname}:${window.location.port}`;
    this.nodesetAxios = axios.create({
      baseURL: `${this.baseURL}`,
      headers: {
        "Content-Type": "text/plain;charset=UTF-8",
        "Access-Control-Allow-Origin": "*"
      }
    });
  }

  //#region documentation
  getDocumentation(relativePath) {
    window.open(`${this.baseURL}/documentation/${relativePath}`);
  }

  deleteDocumentation(relativePath) {
    return this.nodesetAxios.delete("/documentation/", { params: { path: relativePath } });
  }
  //#endregion
  //#region basicNodesetOperations
  loadNodesets() {
    return this.nodesetAxios.get("/nodesets/")
      .then(response => (response.data));
  }

  getRawNodeset(relativePath) {
    window.open(`${this.baseURL}/nodeset/${relativePath}`);
  }

  openNodeSetXMLWithExportedDocumentation(relativePath) {
    window.open(`${this.baseURL}/export/${relativePath}`);
  }

  deleteFileOrFolder(relativePath) {
    return this.nodesetAxios.delete("/nodeset/", { params: { path: relativePath } });
  }

  postNodesetFile(nodesetFile) {
    const formData = new FormData();
    formData.append('nodeset', nodesetFile, nodesetFile.name);
    return this.nodesetAxios.post("/nodeset/", formData);
  }

  uploadFromUrl(nodesetUrl, authorization) {
    const formData = new FormData();
    formData.append('url', nodesetUrl);
    if (authorization != null)
      formData.append('authorization', authorization)
    return this.nodesetAxios.post("/nodeset/url/", formData);
  }
  //#endregion
  //#region generateForNodesetFromUrl
  generateDocumentationUrlDefaultTemplate(nodesetUrl, authorization) {
    const formData = new FormData();
    formData.append('url', nodesetUrl);
    if (authorization != null)
      formData.append('authorization', authorization)
    return this.nodesetAxios.post("/nodeset/generate-from-url/default-html-template/", formData);
  }
  
  generateDocumentationUrlNewTemplate(nodesetUrl, authorization, templateFile, templateDescription) {
    const formData = new FormData();
    formData.append('url', nodesetUrl);
    if (authorization != null)
      formData.append('authorization', authorization)
    
    formData.append('htmlTemplate', templateFile, templateFile.name);
    if (templateDescription != null)
      formData.append('templateDescription', templateDescription);
    return this.nodesetAxios.post("/nodeset/generate-from-url/", formData);
  }
  
  generateDocumentationUrlExistingTemplate(nodesetUrl, authorization, templatePath) {
    const formData = new FormData();
    formData.append('url', nodesetUrl);
    if (authorization != null)
      formData.append('authorization', authorization);
    formData.append('htmlTemplate', templatePath);
    return this.nodesetAxios.post("/nodeset/generate-from-url/existing-html-template/", formData);
  }

  //#endregion
  //#region generateForNewNodesetFile
  generateDocumentationNewNodesetNewTemplate(nodesetFile, templateFile, templateDescription) {
    const formData = new FormData();
    formData.append('nodeset', nodesetFile, nodesetFile.name);
    formData.append('htmlTemplate', templateFile, templateFile.name);
    if (templateDescription != null)
      formData.append('templateDescription', templateDescription);
    return this.nodesetAxios.post("/nodeset/generate/", formData);
  }

  generateDocumentationNewNodesetDefaultTemplate(nodesetFile) {
    const formData = new FormData();
    formData.append('nodeset', nodesetFile, nodesetFile.name);
    return this.nodesetAxios.post("/nodeset/generate/default-html-template/", formData);
  }

  generateDocumentationNewNodesetExistingTemplateFile(nodesetFile, templatePath) {
    const formData = new FormData();
    formData.append('nodeset', nodesetFile, nodesetFile.name);
    formData.append('htmlTemplate', templatePath);
    return this.nodesetAxios.post("/nodeset/generate/existing-template/", formData);
  }

  //#endregion
  //#region generateForExistingNodesetFile
  generateDocumentationExistingNodesetDefaultTemplate(relativePath) {
    const formData = new FormData();
    formData.append('relativePath', relativePath);
    return this.nodesetAxios
      .post("/nodeset/generate-for-existing-nodeset/default-html-template/", formData);
  }

  generateDocumentationExistingNodesetNewTemplateFile(relativePath, templateFile, templateDescription) {
    const formData = new FormData();
    formData.append('relativePath', relativePath);
    formData.append('htmlTemplate', templateFile, templateFile.name);

    if (templateDescription != null)
      formData.append('templateDescription', templateDescription);
    return this.nodesetAxios
      .post("/nodeset/generate-for-existing-nodeset/new-template/", formData);
  }

  generateDocumentationExistingNodesetExistingTemplateFile(relativePath, templatePath) {
    const formData = new FormData();
    formData.append('relativePath', relativePath);
    formData.append('htmlTemplate', templatePath);
    return this.nodesetAxios
      .post("/nodeset/generate-for-existing-nodeset/existing-template/", formData);
  }
  //#endregion
}
