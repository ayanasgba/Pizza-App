/*=============== SHOW MENU ===============*/
const navMenu = document.getElementById("nav-menu"),
  navToggle = document.getElementById("nav-toggle"),
  navClose = document.getElementById("nav-close");

/* Menu show */
if (navToggle) {
  navToggle.addEventListener("click", () => {
    navMenu.classList.add("show-menu");
  });
}

/* Menu hidden */
if (navClose) {
  navClose.addEventListener("click", () => {
    navMenu.classList.remove("show-menu");
  });
}

/*=============== REMOVE MENU MOBILE ===============*/

const navLink = document.querySelectorAll(".nav__link");

const linkAction = () => {
  const navMenu = document.getElementById("nav-menu");
  // When we click on each nav__link, we remove the show-menu class
  navMenu.classList.remove("show-menu");
};
navLink.forEach((n) => n.addEventListener("click", linkAction));

/*=============== ADD SHADOW HEADER ===============*/

const shadowHeader = () => {
  const header = document.getElementById("header");
  // Add a class if the bottom offset is greater than 50 of the viewport
  this.scrollY >= 50
    ? header.classList.add("shadow-header")
    : header.classList.remove("shadow-header");
};
window.addEventListener("scroll", shadowHeader);

/*=============== SWIPER POPULAR ===============*/
const swiperPopular = new Swiper(".popular__swiper", {
  loop: true, // Включаем бесконечный цикл
  grabCursor: true,
  slidesPerView: "auto",
  centeredSlides: true,
  spaceBetween: 30, // Расстояние между слайдами
  pagination: {
    el: ".swiper-pagination",
    clickable: true, // Делаем пагинацию кликабельной
  },
});
/*=============== SHOW SCROLL UP ===============*/

const scrollUp = () => {
  const scrollUp = document.getElementById("scroll-up");

  this.scrollY >= 350
    ? scrollUp.classList.add("show-scroll")
    : scrollUp.classList.remove("show-scroll");
};
window.addEventListener("scroll", scrollUp);

/*=============== SCROLL SECTIONS ACTIVE LINK ===============*/

// const sections = document.querySelectorAll("section[id]");

// const scrollActive = () => {
//   const scrollDown = window.scrollY;

//   sections.forEach((current) => {
//     const sectionHeight = current.offsetHeight,
//       sectionTop = current.offsetTop - 58,
//       sectionId = current.getAttribute("id"),
//       sectionsClass = document.querySelector(
//         ".nav__menu a[href*=" + sectionId + "]"
//       );

//     if (scrollDown > sectionTop && scrollDown <= sectionTop + sectionHeight) {
//       sectionsClass.classList.add("active-link");
//     } else {
//       sectionsClass.classList.remove("active-link");
//     }
//   });
// };
// window.addEventListener("scroll", scrollActive);

// Active link logic for pages
const setActiveLink = () => {
  const currentPath = window.location.pathname; // Get current page path
  const navLinks = document.querySelectorAll(".nav__link"); // Select all nav links

  navLinks.forEach((link) => {
    // Check if the href matches the current path
    if (link.getAttribute("href") === currentPath) {
      link.classList.add("active-link");
    } else {
      link.classList.remove("active-link");
    }
  });
};

// Execute the function on page load
document.addEventListener("DOMContentLoaded", setActiveLink);

/*=============== SCROLL REVEAL ANIMATION ===============*/
const sr = ScrollReveal({
  origin: "top",
  distance: "60px",
  duration: 2500,
  delay: 300,
  // reset: true,
});

sr.reveal(`.home__data,  .popular__container, .footer`);
sr.reveal(`.home__board`, { delay: 700, distance: "100px", origin: "right" });
sr.reveal(`.home__pizza`, {
  delay: 1400,
  distance: "100px",
  origin: "bottom",
  rotate: { z: -90 },
});
sr.reveal(`.home__ingredient`, {
  delay: 2000,
  interval: 100,
});
sr.reveal(`.about__data, .recipe__list, .contact__data`, { origin: "right" });
sr.reveal(`.about__img, .recipe__img, .contact__image`, { origin: "left" });
sr.reveal(`.pizzas__card`, { interval: 100 });
