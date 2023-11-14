<template>
  <v-dialog v-model="show" width="700">
    <v-card :color="color">
      <v-card-title class="headline grey lighten-2">{{ title }}</v-card-title>

      <v-card-text v-if="text != null && typeof text == 'object'">
        <p v-for="text in text" :key="text">{{ text }}</p>
      </v-card-text>

      <v-card-text v-else-if="text != null">{{ text }}</v-card-text>

      <v-divider></v-divider>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="hideDialog">OK</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>


<script>
import Vuetify from "vuetify";
export default {
  name: "InfoDialog",
  vuetify: new Vuetify(),
  props: {
    title: String,
    text: String,
    color: {
      type: String,
      default: "blue lighten-4",
    },
    show: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
      hideDialog: function(){
          this.$emit("hide-dialog");
      }
  }

};
</script>