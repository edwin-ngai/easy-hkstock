<template>
  	<v-app id="easy-hkstock">
	  	<v-navigation-drawer temporary v-model="menuDrawer" class="clipped-drawer">
	  		<v-list>
	        <v-list-tile v-for="menu in menus" :key="menu.title" :to="{path: menu.href}">
	          <v-list-tile-action>
	            <v-list-tile-title>{{ menu.title }}</v-list-tile-title>
	          </v-list-tile-action>
	        </v-list-tile>
      	</v-list>
	  	</v-navigation-drawer>
  		<v-toolbar fixed>
	  		<v-toolbar-side-icon  @click.stop="menuDrawer = !menuDrawer"></v-toolbar-side-icon>
	  		<v-spacer></v-spacer>
	  		<v-btn icon @click.stop="accountDrawer = !accountDrawer">
      			<v-icon >account_circle</v-icon>
    		</v-btn>
  		</v-toolbar>
  		<v-navigation-drawer temporary right v-model="accountDrawer" class="clipped-drawer">
      	<v-list>
	        <v-list-tile>
	          <v-list-tile-action>
	            <v-list-tile-title>Preference</v-list-tile-title>
	          </v-list-tile-action>
	        </v-list-tile>
      	</v-list>
    	</v-navigation-drawer>
 			<main>
    		<v-container fluid>
      			<v-fade-transition mode="out-in">
              <router-view></router-view>
            </v-fade-transition>
    		</v-container>
  		</main>
  		<v-footer></v-footer>
	</v-app>
</template>

<script>
export default {
  props: {
    menus: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      menuDrawer: false,
      accountDrawer: false
    }
  }
}
</script>
<style>
.clipped-drawer {
	    margin-top: 64px;
}

</style>


