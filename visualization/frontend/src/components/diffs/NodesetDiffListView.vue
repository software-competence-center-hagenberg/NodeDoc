<template>
  <v-card class="card d-flex flex-column" :loading="loading">
    <v-card-title
      class="card-title d-flex flex-row align-center justify-space-between"
    >
      Generated Diffs
    </v-card-title>
    <v-card-text class="card-content d-flex flex-column">
      <div class="scroll-content">
        <v-list dense>
          <div v-for="(item, i) in diffs" :key="i">
            <v-divider v-if="i > 0"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title>{{ item.base }}</v-list-item-title>
                <v-list-item-title
                  ><font-awesome-icon
                    style="margin-right: 10px"
                    icon="exchange-alt"
                  />{{ item.compare }}</v-list-item-title
                >
                <v-list-item-subtitle
                  >Generated: {{ item.generated }}</v-list-item-subtitle
                >
              </v-list-item-content>
              <v-list-item-action class="d-flex flex-row align-center">
                <v-btn
                  small
                  color="primary"
                  class="mr-2"
                  @click="openDiff(item)"
                >
                  Open Diff
                </v-btn>
                <v-btn icon @click="deleteItem(item)">
                  <font-awesome-icon icon="trash" />
                </v-btn>
              </v-list-item-action>
            </v-list-item>
          </div>
        </v-list>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import Vuetify from "vuetify";
import NodesetDiffRestResource from "../rest/NodesetDiffRestResource";

const diffRestResource = new NodesetDiffRestResource();

export default {
  name: "NodesetDiffListView",
  vuetify: new Vuetify(),

  data: () => ({
    diffs: [],
    loading: false,
  }),
  props: {},
  computed: {},
  mounted: function () {
    this.loadDiffs();
  },
  methods: {
    loadDiffs: async function () {
      this.loading = true;
      const diffs = await diffRestResource.loadDiffs();
      this.diffs = diffs.data;
      this.loading = false;
    },

    deleteItem: function (item) {
      this.$emit("delete-item", item);
    },

    openDiff(item) {
      diffRestResource.getDiff(item.diffPath);
    },
  },
};
</script>

<style>
.card {
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
