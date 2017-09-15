<template>
  	<v-app id="easy-hkstock" toolbar fill-height>
	  	<v-navigation-drawer temporary :mini-variant.sync="mini">
	  	</v-navigation-drawer>
	  	<v-navigation-drawer temporary right :mini-variant.sync="mini">
	  	</v-navigation-drawer>
  		<v-toolbar fixed dense>
	  		<v-toolbar-side-icon></v-toolbar-side-icon>
	  		<v-spacer></v-spacer>
	  		<v-btn icon>
      			<v-icon>account-circle</v-icon>
    		</v-btn>
  		</v-toolbar>
 		<main>
    		<v-container fluid>
      			<router-view></router-view>
    		</v-container>
  		</main>
  		<v-footer></v-footer>
	</v-app>
</template>

<script>
export default {
  name: 'app'
}
</script>


