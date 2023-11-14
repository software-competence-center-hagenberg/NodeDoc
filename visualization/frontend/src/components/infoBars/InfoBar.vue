<template>
  <v-snackbar :value="show" :color="color" timeout="-1">
    {{ message }}
    <v-progress-linear
      :active="loading"
      :indeterminate="loading"
      absolute
      bottom
      color="deep-purple accent-4"
    ></v-progress-linear>
  </v-snackbar>
</template>

<script>
import Vuetify from "vuetify";

export default {
  name: "InfoBar",
  vuetify: new Vuetify(),
  props: {
    message: String,
    color: {
        type: String,
        default: "info"
    },
    show: {
        type: Boolean,
        default: false
    },
    loading: {
        type: Boolean,
        default: false
    }
  }
}
</script>
