<template>
  <v-dialog v-model="showDialog" width="700">
    <v-card>
      <v-card-title>Select second NodeSet for Diff</v-card-title>
      <nodeset-tree-view style="height: 50vh"
        :selected-node-set-for-diff="baseFile?.nodesetPath"
        :show-non-diff-options="false"
        @show-compare-dialog="onCompareNodes"
      />
      <v-card-actions>
        <v-card-text>Selected base version:<br>{{baseFile?.nodesetPath}}</v-card-text>
        <v-spacer></v-spacer>
        <v-btn @click="hideDialog">Cancel</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style>
  .treeview-card {
    box-shadow: none !important; /* Hide shadow of inner card */
  }
</style>

<script>
import NodesetTreeView from "../treeview/NodesetTreeView.vue";
import NodesetDiffRestResource from "../rest/NodesetDiffRestResource.js";

const nodesetDiffRestResourceService = new NodesetDiffRestResource();
export default {
  name: "CompareNodesetsDialog",
  props: {
    baseFile: null,
  },
  components: {
    NodesetTreeView,
  },
  computed: {
    showDialog: function () {
      return this.baseFile != null;
    },
  },

  methods: {
    hideDialog: function () {
      this.$emit("hide-dialog");
    },

    onCompareNodes: async function (item) {
      this.hideDialog();
      try {
        await nodesetDiffRestResourceService.generateDiff(this.baseFile.nodesetPath, item.nodesetPath);
        this.$emit("generation-done");
      } catch (e) {
        this.$emit("generation-failed", e.response.data);
      }
    },
  },
};
</script>


