<template>
  <v-container>
    <template-radio-group
      :templates="templates"
      @template-selection-changed="setSelectedTemplate"
      ref="templateRadioGroup"
    />

    <template-upload
      @upload-template="uploadTemplateFile"
      ref="templateUpload"
    />
  </v-container>
</template>

<script>
import Vuetify from "vuetify";
import TemplateRadioGroup from "./TemplateRadioGroup.vue";
import TemplateUpload from "../../../templates/TemplateUpload.vue";
import TemplateRestResource from "../../../rest/TemplateRestResource";

const templateRestResourceService = new TemplateRestResource();

export default {
  name: "TemplateSelection",
  vuetify: new Vuetify(),
  components: {
    TemplateRadioGroup,
    TemplateUpload,
  },
  data: () => ({
    templates: Array,
    uploading: false,
  }),

  mounted: function () {
    this.uploading = true;
    this.loadTemplates();
  },
  methods: {
    /**
     * Loads the html templates.
     */
    loadTemplates: function () {
      templateRestResourceService.loadTemplates().then((result) => {
        this.templates = result;
        this.uploading = false;
        this.$refs.templateUpload.unsetTemplateFile();
      });
    },

    uploadTemplateFile: function (template) {
      templateRestResourceService
        .uploadTemplate(template.file, template.description)
        .then(() => {
          this.loadTemplates();
        })
        .catch((error) => {
          this.loadTemplates();
          this.$emit("upload-error", error.response.data, "red lighten-3");
        });
    },

    setSelectedTemplate: function (selected) {
      this.$emit("template-selection-changed", selected);
    },

    unsetSelectedTemplate: function () {
      this.$refs.templateRadioGroup.unsetSelectedTemplate();
    },
  },
};
</script>