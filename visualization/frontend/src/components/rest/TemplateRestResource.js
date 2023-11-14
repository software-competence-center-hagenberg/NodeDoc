import axios from 'axios'

export default class TemplateRestResource {

  constructor() {
    this.templateAxios = axios.create({
      baseURL: `${window.location.protocol}//${window.location.hostname}:${window.location.port}`,
      headers: {
        "Content-Type": "text/plain;charset=UTF-8",
        "Access-Control-Allow-Origin": "*"
      }
    });
  }

  loadTemplates() {
    return this.templateAxios.get("/templates/")
      .then(response => (response.data));
  }

  delete(templateName) {
    return this.templateAxios.delete("/template/", { params: { path: templateName } });
  }

  uploadTemplate(templateFile, templateDescription) {
    const formData = new FormData();
    formData.append('htmlTemplate', templateFile, templateFile.name);
    if (templateDescription != null)
      formData.append('templateDescription', templateDescription);
    return this.templateAxios.post("/template/", formData);
  }

}