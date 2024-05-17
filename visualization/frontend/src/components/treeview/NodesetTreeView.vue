<template>
  <v-card class="treeview-card d-flex flex-column" :loading="loading">
    <v-card-title class="card-title"
      >Uploaded Nodeset files and generated documentations:</v-card-title
    >
    <v-card-text class="card-content d-flex flex-column">
      <v-text-field
        class="flex-grow-0"
        v-model="fileSearch"
        label="Search"
        outlined
        dense
        flat
        clearable
        clear-icon="mdi-close-circle-outline"
      ></v-text-field>
      <div class="scroll-content">
        <v-treeview
          v-if="files != null"
          ref="nodesetTreeView"
          :items="files"
          :search="fileSearch"
          :style="treeviewHeight"
          item-key="id"
          return-object
          open-on-click
          dense
          transition
        >
          <template slot="label" slot-scope="{ item }">
            <nodeset-options
              :item="item"
              :show-selected-for-diff="selectedNodeSetForDiff && (item.nodesetPath === selectedNodeSetForDiff)"
              :show-non-diff-options="showNonDiffOptions"
              @delete-item="deleteItem"
              @generate-documentation="generateDocumentation"
              @show-compare-dialog="onShowCompareDialog"
            />
          </template>
        </v-treeview>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import Vuetify from "vuetify";
import NodesetOptions from "./NodesetOptions.vue";
import NodesetRestResource from "../rest/NodesetRestResource";

const nodesetRestResourceService = new NodesetRestResource();
export default {
  name: "NodesetTreeView",
  vuetify: new Vuetify(),
  components: {
    NodesetOptions,
  },

  data: () => ({
    files: null,
    fileSearch: null,
    loading: false,
  }),
  props: {
    selectedNodeSetForDiff: {
      type: String,
      default: null,
    },
    showNonDiffOptions: {
      type: Boolean,
      default: true,
    }
  },
  computed: {
    treeviewHeight: function () {
      return `height: calc(${this.customHeight} - 8rem)`;
    },
  },
  mounted: function () {
    this.loadNodesetFileStructure();
  },
  methods: {
    /**
     * Loads the nodeset/documentation file structure and opens all nodes of the treeview.
     */
    loadNodesetFileStructure: function () {
      this.loading = true;
      nodesetRestResourceService
        .loadNodesets()
        .then((result) => {
          this.files = result.children;
        })
        .then(() => (this.loading = false));
    },

    deleteItem: function (item) {
      this.$emit("delete-item", item);
    },

    generateDocumentation: function (item) {
      this.$emit("generate-documentation", item);
    },

    onShowCompareDialog: function (item) {
      this.$emit("show-compare-dialog", item);
    },
  },
};
</script>

<style>
.treeview-card {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.card-content {
  flex: 1 1 0;
}

.scroll-content {
  overflow-y: auto;
  flex: 1 1 0;
}

</style>
