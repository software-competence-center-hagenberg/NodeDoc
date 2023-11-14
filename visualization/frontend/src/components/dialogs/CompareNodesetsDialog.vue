<template>
  <v-dialog v-model="showDialog" width="700">
    <nodeset-tree-view style="height: 50vh"
      @show-compare-dialog="onCompareNodes"
    />
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn @click="hideDialog">Cancel</v-btn>
    </v-card-actions>
  </v-dialog>
</template>

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
      await nodesetDiffRestResourceService.generateDiff(this.baseFile.nodesetPath, item.nodesetPath)
      this.$emit("generation-done");
    },
  },
};
</script>


