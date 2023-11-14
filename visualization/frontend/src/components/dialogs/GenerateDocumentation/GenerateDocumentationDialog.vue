<template>
  <v-dialog v-model="showDialog" persistent max-width="700px">
    <v-card>
      <v-card-title class="card-title">
        <span class="headline">
          Generate Documentation for
          <span v-if="selectedNodeset"> {{ selectedNodeset.name }}</span>
          <span v-if="nodesetFile"> {{ nodesetFile.name }}</span>
          <span v-if="nodesetUrl"> {{ urlFileName }}</span>
        </span>
      </v-card-title>
      <v-card-text>
        <template-selection
          @template-selection-changed="setSelectedTemplate"
          @error="onTemplateUploadError"
          ref="templateSelection"
        />
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="hideDialog">Close</v-btn>
        <v-btn color="blue darken-1" text @click="generateDocumentation"
          >Generate</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import Vuetify from "vuetify";
import TemplateSelection from "./template/TemplateSelection.vue";
import NodesetRestResource from "../../rest/NodesetRestResource.js";

const nodesetRestResourceService = new NodesetRestResource();
export default {
  name: "GenerateDocumentationDialog",
  vuetify: new Vuetify(),
  components: {
    TemplateSelection,
  },
  data: () => ({
    selectedTemplate: null,
  }),
  props: {
    selectedNodeset: Object,
    nodesetFile: Object,
    nodesetUrl: Object,
  },
  computed: {
    showDialog: function () {
      return (
        this.selectedNodeset != null ||
        this.nodesetFile != null ||
        this.nodesetUrl != null
      );
    },
    urlFileName: function () {
      return this.nodesetUrl.url.slice(
        this.nodesetUrl.url.lastIndexOf("/") + 1
      );
    },
  },
  methods: {
    hideDialog: function () {
      this.unsetOptions();
      this.$emit("hide-dialog");
    },

    onTemplateUploadError: function (error, color) {
      this.$emit("upload-error", error, color);
    },

    /**
     * Unsets all nodeset and template options.
     */
    unsetOptions: function () {
      this.selectedTemplate = null;
      this.$refs.templateSelection.unsetSelectedTemplate();
    },

    setSelectedTemplate: function (selected) {
      this.selectedTemplate = selected;
    },

    handleResponse: function (type, message) {
      this.unsetOptions();
      this.$emit("generation-ended", type, message);
    },

    generateForSelected: function () {
      if (this.selectedTemplate != null) {
        nodesetRestResourceService
          .generateDocumentationExistingNodesetExistingTemplateFile(
            this.selectedNodeset.nodesetPath,
            this.selectedTemplate
          )
          .then((response) => {
            this.handleResponse("success", response);
          })
          .catch((error) => {
            this.handleResponse("error", error.response.data);
          });
      } else {
        nodesetRestResourceService
          .generateDocumentationExistingNodesetDefaultTemplate(
            this.selectedNodeset.nodesetPath
          )
          .then((response) => {
            this.handleResponse("success", response);
          })
          .catch((error) => {
            this.handleResponse("error", error.response.data);
          });
      }
    },

    generateForFile: function () {
      if (this.selectedTemplate != null) {
        nodesetRestResourceService
          .generateDocumentationNewNodesetExistingTemplateFile(
            this.nodesetFile,
            this.selectedTemplate
          )
          .then((response) => {
            this.handleResponse("success", response);
          })
          .catch((error) => {
            this.handleResponse("error", error.response.data);
          });
      } else {
        nodesetRestResourceService
          .generateDocumentationNewNodesetDefaultTemplate(this.nodesetFile)
          .then((response) => {
            this.handleResponse("success", response);
          })
          .catch((error) => {
            this.handleResponse("error", error.response.data);
          });
      }
    },

    generateForUrl: function () {
      let authorization = null;
      if (this.nodesetUrl.token != null) {
        authorization = `Bearer ${this.nodesetUrl.token}`;
      }
      if (this.selectedTemplate != null) {
        nodesetRestResourceService
          .generateDocumentationUrlExistingTemplate(
            this.nodesetUrl.url,
            authorization,
            this.selectedTemplate
          )
          .then((response) => {
            this.handleResponse("success", response);
          })
          .catch((error) => {
            this.handleResponse("error", error.response.data);
          });
      } else {
        nodesetRestResourceService
          .generateDocumentationUrlDefaultTemplate(
            this.nodesetUrl.url,
            authorization
          )
          .then((response) => {
            this.handleResponse("success", response);
          })
          .catch((error) => {
            this.handleResponse("error", error.response.data);
          });
      }
    },

    generateDocumentation: function () {
      this.generatingDoku = true;
      this.$emit("start-generation");

      if (this.selectedNodeset != null) {
        this.generateForSelected();
      } else if (this.nodesetFile != null) {
        this.generateForFile();
      } else if (this.nodesetUrl != null && this.nodesetUrl.url != null) {
        this.generateForUrl();
      }
      this.hideDialog();
    },
  },
};
</script>


<style>
.card-title {
  font-family: "Segoe UI", Arial, Helvetica, sans-serif;
  padding: 8px !important;
  font-weight: normal;
}
</style>