<script>
import { authComputed } from '@state/helpers'
import NavBarRoutes from './nav-bar-routes.vue'

export default {
  components: { NavBarRoutes },
  data() {
    return {
      persistentNavRoutes: [
        {
          name: 'logging',
          icon: 'comment',
          tooltip: 'open Logging view',
        },
        {
          name: 'configuration',
          icon: 'settings',
          tooltip: 'open Configuration view',
        },
      ],
      secondaryNavRoutes: [
        {
          name: 'about',
          img: 'assets/scoop_logo_rgb_72dpi.png',
          imgWidth: 125,
          imgHeight: 50,
          tooltip: 'open About view',
        },
      ],
    }
  },
  computed: {
    ...authComputed,
  },
}
</script>

<template>
    <nav class="navbar navbar-default">
        <div class="container-fixed">
            <div class="navbar-expanded">
                <div id="MainMenu" class="brandnav brandnav-lvl-1">
                    <ul class="nav">
                        <NavBarRoutes :routes="persistentNavRoutes"/>
                        <slot/>
                    </ul>

                </div>
            </div>
            <div class="navbar-persistent">
                <div id="AboutMenu" class="brandnav brandnav-lvl-1">
                    <ul class="nav">
                        <NavBarRoutes :routes="secondaryNavRoutes"/>
                    </ul>

                </div>
            </div>
        </div>
    </nav>
</template>
