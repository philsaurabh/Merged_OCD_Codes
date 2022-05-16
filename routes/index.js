const express = require('express');
const router = express.Router();
const { ensureAuthenticated } =require('../config/auth');

//Welcome Page
router.get('/', (req,res) => res.render('welcome'));

//Dashboard
router.get('/dashboard', ensureAuthenticated, (req,res) => 
res.render('dashboard', {
    name: req.user.name,
    email: req.user.email,
    designation: req.user.designation,
    username: req.user.username,
    /*img: {
        data: fs.readFileSync(path.join(__dirname + '/uploads/' + req.file.filename)),
        contentType: 'image/png'
    }   */
}));

module.exports = router;