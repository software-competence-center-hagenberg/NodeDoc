<template>
  <div class="container">
    <div class="name" v-if="item.name != null && item.children">
      {{ item.name }}
    </div>
    <div v-if="!item.children" class="actions">
      <v-btn class="xml-btn" small @click="showCompareDialog(item)">
        <font-awesome-icon icon="exchange-alt" />
      </v-btn>
      <v-btn
        small
        class="xml-btn"
        v-if="item.nodesetPath"
        @click="getRawNodeset(item)"
      >
        XML
        <v-divider vertical class="mx-2" />
        <v-btn :disabled="this.$store.state.isDemo" icon @click.stop="deleteNodeset(item)">
          <font-awesome-icon icon="trash" />
        </v-btn>
      </v-btn>
      <v-btn
        class="xml-btn"
        small
        v-if="item.nodesetPath"
        @click.stop="generateDocumentation(item)"
      >
        <font-awesome-icon icon="cogs" />
      </v-btn>

      <v-btn
        class="docu-btn"
        small
        v-if="item.docuPath"
        @click="getDocumentation(item)"
        >HTML
        <v-divider vertical class="mx-2" />
        <v-btn icon @click.stop="deleteDocumentation(item)">
          <font-awesome-icon icon="trash" />
        </v-btn>
      </v-btn>
      <v-btn
        class="docu-btn"
        small
        v-if="item.docuPath"
        @click="openNodeSetXMLWithExportedDocumentation(item)"
        >
          Export
      </v-btn>
    </div>
    <div v-if="item.children" class="actions folder-actions">
      <v-btn :disabled="this.$store.state.isDemo" icon @click.stop="deleteFolder(item)">
        <font-awesome-icon icon="trash" />
      </v-btn>
    </div>
  </div>
</template>

<script>
import NodesetRestResource from "../rest/NodesetRestResource";

const nodesetRestResourceService = new NodesetRestResource();

export default {
  name: "NodesetOptions",
  props: {
    item: null,
  },
  methods: {
    /**
     * Gets the documentation for a nodeset by its path.
     */
    getDocumentation: function (item) {
      nodesetRestResourceService.getDocumentation(item.docuPath);
    },

    getRawNodeset: function (item) {
      nodesetRestResourceService.getRawNodeset(item.nodesetPath);
    },

    openNodeSetXMLWithExportedDocumentation: function (item) {
      nodesetRestResourceService.openNodeSetXMLWithExportedDocumentation(item.nodesetPath);
    },

    deleteFolder(item) {
      let tmp = {
        name: item.name,
        path: item.path,
      };
      this.$emit("delete-item", tmp);
    },

    deleteNodeset(item) {
      let tmp = {
        name: item.name + ".xml",
        path: item.nodesetPath,
      };
      this.$emit("delete-item", tmp);
    },

    deleteDocumentation(item) {
      let tmp = {
        name: item.name + ".html",
        docu: true,
        path: item.docuPath,
      };
      this.$emit("delete-item", tmp);
    },

    generateDocumentation(item) {
      this.$emit("generate-documentation", item);
    },

    showCompareDialog(item){
      this.$emit("show-compare-dialog", item);
    }
  },
};
</script>

<style scoped>
.container {
  min-height: 2.5rem;
}
.file-actions {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.folder-actions {
  margin: 0;
  position: absolute;
  top: 50%;
  right: 1rem;
  -ms-transform: translateY(-50%);
  transform: translateY(-50%);
}

.name {
  float: left;
}

.actions {
  float: left;
  margin-bottom: 0.2rem;
}

.xml-btn {
  margin: 3px 3px;
  float: left;
}

.docu-btn {
  margin: 3px 3px;
  float: left;
}
</style>