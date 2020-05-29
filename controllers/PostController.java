package com.codeup.springblogapp.controllers;

import com.codeup.springblogapp.model.Post;
import com.codeup.springblogapp.model.User;
import com.codeup.springblogapp.repositories.PostRepository;
import com.codeup.springblogapp.repositories.UserRepository;
import com.codeup.springblogapp.services.EmailService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {

    private UserRepository userDao; // this has all the functions of JpaRepository
    private PostRepository postDao; // this has all the functions of JpaRepository
    private EmailService emailService;

    public PostController(UserRepository userDao, PostRepository postDao, EmailService emailService) {
        this.userDao = userDao;
        this.postDao = postDao;
        this.emailService = emailService;
    }

    @GetMapping("/posts")
    public String showPostsIndexPage(Model model) {
        model.addAttribute("posts", postDao.findAll());
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String showAnIndividualPost(@PathVariable long id, Model model) {
        Post thisPost = postDao.getOne(id); // using JpaRepository.getOne, but for our Post objects
        model.addAttribute("post", thisPost);
        return "posts/show";
    }

    @GetMapping("/posts/create")
    public String createPost(Model model) {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj == null || !(obj instanceof UserDetails)) {
            return "redirect:/login";
        }
        User user = (User) obj;
        Post post = new Post();
        post.setUser(user);
        model.addAttribute("post", post);
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String submitCreatePost(@ModelAttribute Post post) {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj == null || !(obj instanceof UserDetails)) {
            return "redirect:/login";
        }
        User user = (User) obj;
        post.setUser(user);
        postDao.save(post);
        emailService.prepareAndSend(post, "CREATED Post: " + post.getTitle(),
                post.getTitle() +"\n\n" +
                        post.getBody());
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditForm(@PathVariable long id, Model model) {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj == null || !(obj instanceof UserDetails)) {
            return "redirect:/login";
        }
        User user = (User) obj;
        Post post = postDao.getOne(id);
        if (post.getUser().getId() != user.getId()) {
            return "redirect:/posts/" + post.getId();
        }
        model.addAttribute("post", post);
        return "/posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String editPost(@PathVariable long id, @ModelAttribute Post post) {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj == null || !(obj instanceof UserDetails)) {
            return "redirect:/login";
        }
        User user = (User) obj;
        post.setId(id);
        post.setUser(user);
        postDao.save(post);
        emailService.prepareAndSend(post, "EDITED post: " + post.getTitle(),
                post.getTitle() +"\n\n" +
                        post.getBody());
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/editcreate")
    public String editCreatePost(Model model) {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj == null || !(obj instanceof UserDetails)) {
            return "redirect:/login";
        }
        User user = (User) obj;
        Post post = new Post();
        post.setUser(user);
        model.addAttribute("post", post);
        return "/posts/edit";
    }

    @PostMapping("/posts/{id}/delete")
    public String deleteAd(@PathVariable long id, Model model) {
        postDao.deleteById(id);
        return "redirect:/posts";
    }
}
