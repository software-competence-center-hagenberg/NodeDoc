<template>
  <div>
    <div v-cloak @drop.prevent="handleDroppedNodesetFile" @dragover.prevent>
      <v-file-input
        v-model="nodesetFile"
        accept=".xml"
        label=".xml file"
        hint="Click here to select a nodeset .xml file. Or drag a file over"
        :disabled="disabled"
        persistent-hint
        outlined
        dense
      ></v-file-input>
    </div>
  </div>
</template>

<script>
import Vuetify from "vuetify";

export default {
  name: "NodesetFileUploadTab",
  vuetify: new Vuetify(),
  components: {},
  data: () => ({
    nodesetFile: null,
  }),
  props: {
    disabled: Boolean, 
  },
  watch: {
    nodesetFile: function () {
      this.$emit("file-changed", this.nodesetFile);
    },
  },
  methods: {
    handleDroppedNodesetFile(e) {
      let droppedFiles = e.dataTransfer.files;
      if (!droppedFiles) return;
      [...droppedFiles].forEach((f) => {
        if (f.type == "text/xml") {
          this.nodesetFile = f;
        }
      });
    },

    unsetNodesetFile() {
        this.nodesetFile = null;
    }
  },
};
</script>