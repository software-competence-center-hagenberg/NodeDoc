<template>
  <v-dialog v-model="showDialog" v-if="fileToDelete != null" width="700">
    <v-card color="info">
      <v-card-title class="headline grey lighten-2">
        Are you sure you want to delete
        {{
          fileToDelete.name != null
            ? fileToDelete.name
            : fileToDelete.templateName
        }}?
      </v-card-title>

      <v-divider></v-divider>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="deleteFile(fileToDelete)" color="success">OK</v-btn>
        <v-btn @click="cancelFileDelete" color="error">Cancel</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import Vuetify from "vuetify";
import NodesetRestResource from "../rest/NodesetRestResource";
import NodesetDiffRestResource from "../rest/NodesetDiffRestResource";
import TemplateRestResource from "../rest/TemplateRestResource";

const nodesetRestResourceService = new NodesetRestResource();
const nodesetDiffRestResourceService = new NodesetDiffRestResource();
const templateRestResourceService = new TemplateRestResource();

export default {
  name: "NodesetTreeView",
  vuetify: new Vuetify(),
  data: () => ({
    showDialog: false,
  }),
  props: {
    fileToDelete: Object,
  },
  watch: {
    fileToDelete: function () {
      this.showDialog = this.fileToDelete != null;
    },
  },
  methods: {
    deleteFile: function (file) {
      if (file.docu) this.deleteDocumentation(file);
      else if (file.templateName != null) this.deleteTemplate(file);
      else if (file.diffPath != null) this.deleteDiff(file);
      else this.deleteFileOrFolder(file);
    },

    deleteDiff: function(diff) {
      nodesetDiffRestResourceService.deleteDiff(diff.diffPath).then(() => {
        this.$emit("load-file-structure");
      });
    },

    deleteTemplate: function (template) {
      templateRestResourceService.delete(template.templateName).then(() => {
        this.$emit("load-file-structure");
      });
    },

    /**
     * Deletes a documentation using the path.
     */
    deleteDocumentation: function (item) {
      nodesetRestResourceService.deleteDocumentation(item.path).then(() => {
        this.$emit("load-file-structure");
      });
    },

    /**
     * Deletes a nodeset specified by its path
     */
    deleteFileOrFolder: function (item) {
      nodesetRestResourceService.deleteFileOrFolder(item.path).then(() => {
        this.$emit("load-file-structure");
      });
    },

    cancelFileDelete: function () {
      this.$emit("cancel-delete");
    },
  },
};
</script>