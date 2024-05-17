<template>
  <v-app>
    <Header />
    <div v-if="modt.length > 0" class="motd">
      {{ modt }}
    </div>
    <v-main>
      <splitpanes vertical class="default-theme">
        <pane style="min-width: 35rem">
          <splitpanes horizontal class="default-theme">
            <pane class="content-pane">
              <nodeset-tree-view
                @delete-item="deleteItem"
                @generate-documentation="generateDocumentation"
                @show-compare-dialog="onShowCompareDialog"
                ref="nodesetTreeView"
              />
            </pane>
            <pane class="content-pane">
              <nodeset-diff-list-view
                ref="diffList"
                @delete-item="deleteItem"
              />
            </pane>
          </splitpanes>
        </pane>
        <pane class="content-pane" style="min-width: 30rem; display: flex; flex-direction: column;">
          <nodeset-upload-card
            style="margin-bottom: 10px"
            @generate-for-file="onGenerateForFile"
            @generate-for-url="onGenerateForUrl"
            @reload-data="onReloadData"
            @upload-error="onUploadError"
            ref="nodesetUploadCard"
          />
          <template-list
            style="flex-grow: 1;"
            @delete-item="deleteItem"
            @upload-error="onUploadError"
            ref="templateList"
          />
        </pane>
      </splitpanes>
      <delete-file-dialog
        :fileToDelete="fileToDelete"
        @cancel-delete="fileToDelete = null"
        @load-file-structure="loadFileStructure"
      />
      <generate-documentation-dialog
        :selectedNodeset="selectedNodeset"
        :nodesetFile="nodesetFile"
        :nodesetUrl="nodesetUrl"
        @hide-dialog="unsetNodesetOptions"
        @start-generation="onGenerate"
        @generation-ended="endGenerating"
        @upload-error="onUploadError"
        ref="generateDocuDialog"
      />
      <compare-nodesets-dialog
        :baseFile="compareBaseFile"
        @hide-dialog="compareBaseFile = null"
        @generation-done="$refs.diffList.loadDiffs()"
        @generation-failed="onDiffError"
      />
      <info-dialog
        :title="infoDialog.title"
        :text="infoDialog.text"
        :color="infoDialog.color"
        :show="infoDialog.show"
        @hide-dialog="infoDialog.show = false"
      />
    </v-main>
    <v-footer>
      <info-bar
        :message="infoBar.message"
        :show="infoBar.show"
        :loading="infoBar.loading"
        :color="infoBar.color"
      />
    </v-footer>
  </v-app>
</template>

<script>
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";
import Header from "./components/Header";
import NodesetTreeView from "./components/treeview/NodesetTreeView.vue";
import DeleteFileDialog from "./components/dialogs/DeleteFileDialog.vue";
import NodesetUploadCard from "./components/nodesetUpload/NodesetUploadCard.vue";
import TemplateList from "./components/templates/TemplateList.vue";
import GenerateDocumentationDialog from "./components/dialogs/GenerateDocumentation/GenerateDocumentationDialog.vue";
import CompareNodesetsDialog from "./components/dialogs/CompareNodesetsDialog.vue";
import InfoDialog from "./components/dialogs/InfoDialog.vue";
import InfoBar from "./components/infoBars/InfoBar.vue";
import NodesetDiffListView from "./components/diffs/NodesetDiffListView.vue";
import ApplicationRestResource from './components/rest/ApplicationRestResource';

export default {
  name: "App",

  components: {
    Header,
    NodesetTreeView,
    DeleteFileDialog,
    NodesetUploadCard,
    TemplateList,
    GenerateDocumentationDialog,
    CompareNodesetsDialog,
    InfoDialog,
    InfoBar,
    Splitpanes,
    Pane,
    NodesetDiffListView,
  },

  data: () => ({
    fileToDelete: null,
    selectedNodeset: null,
    nodesetFile: null,
    nodesetUrl: null,
    compareBaseFile: null,
    infoBar: {
      show: false,
      loading: false,
      color: "info",
      message: null,
    },
    infoDialog: {
      show: false,
      title: null,
      text: null,
      color: "blue lighten-4",
    },
    modt: "",
  }),

  mounted: async function() {
    const restResource = new ApplicationRestResource();
    this.modt = await restResource.getMotd();
  },

  methods: {
    deleteItem: function (item) {
      this.fileToDelete = item;
    },

    loadFileStructure: function () {
      this.$refs.nodesetTreeView.loadNodesetFileStructure();
      this.$refs.nodesetUploadCard.unsetOptions();
      this.$refs.templateList.loadTemplates();
      this.$refs.diffList.loadDiffs();
      this.fileToDelete = null;
    },

    unsetNodesetOptions: function () {
      this.$refs.nodesetUploadCard.unsetOptions();
      this.selectedNodeset = null;
      this.nodesetFile = null;
      this.nodesetUrl = null;
    },

    setInfoDialogValues: function (
      title,
      text,
      show = true,
      color = "blue lighten-4"
    ) {
      this.infoDialog = {
        show: show,
        title: title,
        text: text,
        color: color,
      };
    },

    onGenerate: function () {
      this.infoBar.color = "info";
      this.infoBar.show = true;
      this.infoBar.loading = true;
      this.infoBar.message = "Generating";
    },

    onGenerateForFile: function (file) {
      this.nodesetFile = file;
    },

    onGenerateForUrl: function (url) {
      this.nodesetUrl = url;
    },

    generateDocumentation: function (item) {
      this.selectedNodeset = item;
    },

    onUploadError: function (data, color) {
      this.setInfoDialogValues("Error", data, true, color);
    },

    onDiffError: function (data) {
      this.setInfoDialogValues("Error", data, true, "red lighten-3");
    },

    /**
     * Creates an array containing a standard replacement string and all
     * nodesets that where not available in the publication date but in another
     * date.
     */
    getReplacedNodesetArray: function (replacedObject) {
      var replacedArray = [
        "Following nodesets where used instead of the required nodesets (required -> used instead):",
      ];
      for (let original in replacedObject) {
        replacedArray.push(`${original} -> ${replacedObject[original]}`);
      }
      return replacedArray;
    },

    endGenerating: function (val, response) {
      if (val == "success") {
        if (response.data != "Generated documentation") {
          this.setInfoDialogValues(
            "Replaced Nodesets",
            this.getReplacedNodesetArray(response.data),
            true,
            "blue lighten-3"
          );
        } else {
          this.setInfoDialogValues(
            "Success",
            "Generating documentation successfull",
            true,
            "success"
          );
        }
      } else if (val == "error") {
        this.setInfoDialogValues("Error", response, true, "error");
      }
      this.infoBar.show = false;
      this.unsetNodesetOptions();
      this.loadFileStructure();
    },

    onReloadData: function (color, title, message) {
      this.infoBar.loading = false;
      this.infoBar.message = message;
      this.infoBar.color = color;
      this.infoBar.show = true;
      setTimeout(() => {
        this.infoBar.show = false;
      }, 2000);
      this.loadFileStructure();
    },

    onShowCompareDialog: function (file) {
      this.compareBaseFile = file;
    },
  },
};
</script>

<style lang="scss" scoped>
#app {
  background-image: url("./assets/background.png");
  background-size: contain;
  background-repeat: repeat-y;
}

.content-pane {
  padding: 5px;
}

.v-footer {
  height: 3.5rem;
  background-color: transparent !important;
}

.motd {
  background-color: #fcd34d;
  width: 100%;
  padding: 0.2em;
}
</style>
