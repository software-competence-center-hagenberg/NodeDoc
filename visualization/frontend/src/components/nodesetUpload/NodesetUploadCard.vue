<template>
  <v-card class="upload-card" :disabled="this.$store.state.isDemo" style="cursor: not-allowed;" raised :loading="uploadingFile">
    <v-card-title class="card-title">Upload Nodeset file:</v-card-title>
    <v-card-text style="height: 8rem">
      <v-tabs height="30">
        <v-tab :disabled="urlNodeset.url != null">Nodeset File upload</v-tab>
        <v-tab :disabled="nodesetFile != null">Nodeset URL</v-tab>
        <v-tab-item class="nodeset-tab">
          <nodeset-file-upload-tab
            :disabled="urlNodeset.url != null"
            @file-changed="setNodesetFile"
            ref="fileUploadTab"
          />
        </v-tab-item>
        <v-tab-item class="nodeset-tab">
          <nodeset-url-upload-tab
            :disabled="nodesetFile != null"
            @url-object-changed="setUrlObject"
            ref="urlUploadTab"
          />
        </v-tab-item>
      </v-tabs>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn
        right
        @click="onUpload"
        :disabled="nodesetFile == null && urlNodeset.url == null"
        >Upload</v-btn
      >
      <v-btn
        right
        @click="onGenerate"
        :disabled="nodesetFile == null && urlNodeset.url == null"
        >Generate</v-btn
      >
    </v-card-actions>
  </v-card>
</template>

<script>
import Vuetify from "vuetify";
import NodesetFileUploadTab from "./NodesetFileUploadTab.vue";
import NodesetUrlUploadTab from "./NodesetUrlUploadTab.vue";
import NodesetRestResource from "../rest/NodesetRestResource.js";

const nodesetRestResourceService = new NodesetRestResource();

export default {
  name: "NodesetUploadCard",
  vuetify: new Vuetify(),
  components: {
    NodesetFileUploadTab,
    NodesetUrlUploadTab,
  },
  data: () => ({
    nodesetFile: null,
    uploadingFile: null,
    urlNodeset: {
      url: null,
      token: null,
    },
  }),
  methods: {
    setNodesetFile: function (file) {
      this.nodesetFile = file;
    },

    setUrlObject: function (urlNodeset) {
      this.urlNodeset = urlNodeset;
    },

    /**
     * Unsets all nodeset and template options.
     */
    unsetOptions: function () {
      this.$refs.fileUploadTab.unsetNodesetFile();
      if (this.$refs.urlUploadTab)
        this.$refs.urlUploadTab.unsetNodesetUrlObject();
      this.uploadingFiles = false;
    },

    emitReloadData: function () {
      this.unsetOptions();
      this.$emit(
        "reload-data",
        "success",
        "Success",
        "Uploading file successfull"
      );
    },

    /**
     * Handles the file upload. When the upload button is clicked, the nodeset
     * file is posted and the responses are handled (error when file could not
     * be saved, etc.)
     */
    onUpload: function () {
      this.uploadingFiles = true;

      if (this.nodesetFile != null) {
        nodesetRestResourceService
          .postNodesetFile(this.nodesetFile)
          .then(() => {
            this.emitReloadData();
          })
          .catch((error) => {
            this.unsetOptions();
            this.$emit("upload-error", error.response.data, "red lighten-3");
          });
      } else if (this.urlNodeset.url != null) {
        let authorization = null;
        if (this.urlNodeset.token != null) {
          authorization = `Bearer ${this.urlNodeset.token}`;
        }
        nodesetRestResourceService
          .uploadFromUrl(this.urlNodeset.url, authorization)
          .then(() => {
            this.emitReloadData();
          })
          .catch((error) => {
            this.unsetOptions();
            this.$emit("upload-error", error.response.data, "red lighten-3");
          });
      }
    },

    onGenerate: function () {
      if (this.nodesetFile != null) {
        this.$emit("generate-for-file", this.nodesetFile);
      } else if (this.urlNodeset.url != null) {
        this.$emit("generate-for-url", this.urlNodeset);
      }
    },
  },
};
</script>

<style>
.nodeset-tab {
  margin-top: 5px;
}
</style>