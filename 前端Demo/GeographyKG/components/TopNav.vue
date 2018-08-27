<template>
  <b-navbar toggleable="lg" class="top-navbar">
    <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>
    <b-navbar-brand href="#">Geography KG</b-navbar-brand>
    <b-collapse is-nav id="nav_collapse">
      <!--导航栏选项-->
      <b-navbar-nav>
        <b-nav-item href="/">查询</b-nav-item>
      </b-navbar-nav>
      <!-- Right aligned nav items -->
      <b-navbar-nav class="ml-auto">
        <b-nav-form>
          <b-form-input size="sm" class="mr-sm-2" type="text" placeholder="Search"/>
          <b-button size="sm" class="my-2 my-sm-0" type="submit">Search</b-button>
        </b-nav-form>
        <b-nav-item-dropdown text="Lang" right>
          <b-dropdown-item href="#">EN</b-dropdown-item>
          <b-dropdown-item href="#">ES</b-dropdown-item>
          <b-dropdown-item href="#">RU</b-dropdown-item>
          <b-dropdown-item href="#">FA</b-dropdown-item>
        </b-nav-item-dropdown>
        <b-nav-item-dropdown right>
          <!-- Using button-content slot -->
          <template slot="button-content">
            <em>User</em>
          </template>
          <b-dropdown-item href="#">Profile</b-dropdown-item>
          <b-dropdown-item href="#">Signout</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script>
  export default {
    name: "TopNav"
  }
</script>

<style scoped>
  .top-navbar {
    color: lightgray;
    width: 100%;
    background-color: #357274;
    position: fixed;
    font-size: 18px;
    top: 0;
    z-index: 99;
  }

  @media screen and (min-width: 960px) {
    .top-navbar {
      height: 60px;
    }
  }
  @media screen and (max-width: 959px) {
    .top-navbar {
      height: fit-content;
    }
  }
</style>
