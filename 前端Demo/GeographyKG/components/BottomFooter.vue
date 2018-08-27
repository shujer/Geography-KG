<template>
  <div class="bottom-footer footer">
    <div class="footer-content clearfix" >
      <div class="footer-grid">
        <h4>About Us</h4>
        <p>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu
          fugiat nulla pariatur. <span>Excepteur sint occaecat cupidatat non proident
					sunt in culpa qui officia deserunt mollit anim id est laborum.</span></p>
      </div>
      <div class="footer-grid">
        <h4>Contact</h4>
        <ul style="list-style: none">
          <li><span class="fa fa-address-book fa-lg"></span> 738 Diamond Road, New York City</li>
          <li><span class="fa fa-mail-forward fa-lg"></span><a href="mailto:info@example.com">info@example.com</a></li>
          <li><span class="fa fa-phone fa-lg" aria-hidden="true"></span> (0123) 0111 111 222</li>
        </ul>
      </div>
    </div>
    <div class="footer_copy"><span>Copyright &copy; 2018.Company name All rights reserved.</span><a target="_blank" href="#">STAR MOVIES</a></div>
  </div>
</template>

<script>
  export default {
    name: "BottomFooter"
  }
</script>

<style scoped>
  .bottom-footer{
    background-color:#2e5a5c;
    color: lightgray;
    width: 100%;
    height: fit-content;
    margin: 0;
    text-align: center;
    font-size:15px;
  }

  a {
    color: lightgray;
  }

  .footer-grid {
    width: 30%;
    height: 100%;
    margin: 3% 10% 3% 10%;
    float: left;
    text-align: left;
  }

  .footer_copy {
    text-align: center;
  }
  ul,li{
    list-style-type:none;
    padding:0;
    margin:10px 0px 10px 0px;
  }
</style>
